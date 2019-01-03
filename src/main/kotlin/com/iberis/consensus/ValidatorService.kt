package com.iberis.consensus

import com.iberis.account.Account
import com.iberis.application.ApplicationProperties
import com.iberis.common.logger
import com.iberis.consensus.model.CommitBlock
import com.iberis.core.block.Block
import com.iberis.core.block.BlockService
import com.iberis.db.DatabaseService
import com.iberis.node.service.BroadCastService
import com.iberis.util.createAddress
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Please describe the role of the ValidatorService
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-22
 *
 * @author namjug.kim
 * @since 2018-10-22
 * @version 0.1
 */
@Service
class ValidatorService(private val blockProposerHandler: BlockProposerHandler,
                       private val broadCastService: BroadCastService,
                       private val blockService: BlockService,
                       private val validatorHandler: ValidatorHandler,
                       private val databaseService: DatabaseService,
                       private val forkChoiceRuleService: ForkChoiceRuleService) {

    companion object {
        val logger = logger<ValidatorService>()
    }

    fun proposalBlockEvent(block: Block) {
        val account = ApplicationProperties.myAccount

        if (validatorHandler.findValidator(account.accountId.address) == null) {
            logger.info("im not validator")
            return
        }

        preCommitBlock(block, account)
    }

    private fun preCommitBlock(block: Block, account: Account) {
        forkChoiceRuleService.checkSync()

        val blockProposalRuleAtTime = blockProposerHandler.findBlockProducerAtTime(block.blockHeader.time)
        val currentTime = Instant.now().toEpochMilli()

        if (currentTime < blockProposalRuleAtTime.roundStartTime || currentTime > blockProposalRuleAtTime.roundEndTime) {
            logger.error("[PRE COMMIT] [WRONG TIME] transaction timeout")
            throw RuntimeException("[PRE COMMIT] [WRONG TIME] transaction timeout.")
        }

        if (blockProposalRuleAtTime.blockProducer.accountId.address != block.blockProducerAddress.createAddress()) {
            logger.error("[PRE COMMIT] [WRONG TIME] invalid block producer.")
            throw RuntimeException("[PRE COMMIT] [WRONG TIME] invalid block producer.")
        }

        if (!block.verifySignature()) {
            logger.error("[PRE COMMIT] [INVALID SIGNATURE] invalid block producer signature.")
            throw RuntimeException("[PRE COMMIT] [INVALID SIGNATURE] invalid block producer signature.")
        }

        if (block.blockHash != block.blockHeader.calculateBlockHash()) {
            logger.error("[PRE COMMIT] [WRONG BLOCK HASH] invalid block hash.")
            throw RuntimeException("[PRE COMMIT] [WRONG BLOCK HASH] invalid block hash.")
        }

        val lastBlock = databaseService.findLastBlock() ?: throw RuntimeException("last block is null")
        if (lastBlock.blockHash != block.blockHeader.prevBlockHash) {
            logger.error("[PRE COMMIT] [INVALID BLOCK CHAIN] invalid block chain.")
            throw RuntimeException("[PRE COMMIT] [INVALID BLOCK CHAIN] invalid block chain.")
        }

        val commitBlock = CommitBlock(
                witnessSignature = CommitBlock.generateSignature(account.publicKey, account.privateKey!!, block),
                witness = account.publicKey,
                block = block
        )

        broadCastService.preCommitBlock(commitBlock)
    }

    fun preCommitBlockEvent(commitBlock: CommitBlock) {
        val account = ApplicationProperties.myAccount

        if (validatorHandler.findValidator(account.accountId.address) == null) {
            logger.info("im not validator")
            return
        }

        commitBlock(commitBlock, account)
    }

    private fun commitBlock(commitBlock: CommitBlock, account: Account) {
        forkChoiceRuleService.checkSync()

        val blockProposalRuleAtTime = blockProposerHandler.findBlockProducerAtTime(commitBlock.block.blockHeader.time)
        val currentTime = Instant.now().toEpochMilli()

        if (currentTime < blockProposalRuleAtTime.roundStartTime || currentTime > blockProposalRuleAtTime.roundEndTime) {
            logger.error("[PRE COMMIT] [WRONG TIME] transaction timeout")
            throw RuntimeException("[PRE COMMIT] [WRONG TIME] transaction timeout.")
        }

        if (blockProposalRuleAtTime.blockProducer.accountId.address != commitBlock.block.blockProducerAddress.createAddress()) {
            logger.error("[WRONG TIME] invalid block producer.")
            throw RuntimeException("[WRONG TIME] invalid block producer.")
        }

        if (!commitBlock.verifyWitnessSignature()) {
            logger.error("[PRE COMMIT] [INVALID WITNESS SIGNATURE] invalid witness signature! byzantine witness : {}", commitBlock.witness.createAddress())
            throw RuntimeException("[PRE COMMIT] [INVALID WITNESS SIGNATURE] invalid witness signature!")
        }

        if (commitBlock.block.blockHash != commitBlock.block.blockHeader.calculateBlockHash()) {
            logger.error("[PRE COMMIT] [WRONG BLOCK HASH] invalid block hash.")
            throw RuntimeException("[PRE COMMIT] [WRONG BLOCK HASH] invalid block hash.")
        }

        if (validatorHandler.findValidator(commitBlock.witness.createAddress()) == null) {
            logger.error("[PRE COMMIT] [INVALID WITNESS] invalid witness! byzantine witness : {}", commitBlock.witness.createAddress())
            throw RuntimeException("[PRE COMMIT] [INVALID WITNESS] invalid witness signature!")
        }

        val lastBlock = databaseService.findLastBlock() ?: throw RuntimeException("last block is null")
        if (lastBlock.blockHash != commitBlock.block.blockHeader.prevBlockHash) {
            logger.error("[COMMIT] [INVALID BLOCK CHAIN] invalid block chain.")
            throw RuntimeException("[COMMIT] [INVALID BLOCK CHAIN] invalid block chain.")
        }

        databaseService.addPreCommitBlock(commitBlock)
        val validators = validatorHandler.findValidators()

        var preCommitWitnessCountThreshold = validators.size * 2 / 3
        if (preCommitWitnessCountThreshold < 3) {
            preCommitWitnessCountThreshold = validators.size
        }

        // consensus preCommit!
        val preCommitWitnessCount = databaseService.findAllPreCommitBlock(commitBlock.block.blockHash).size
        logger.debug("[PRE COMMIT] preCommitWitnessCount : {}, preCommitWitnessCountThreshold : {}", preCommitWitnessCount, preCommitWitnessCountThreshold)
        if (preCommitWitnessCount >= preCommitWitnessCountThreshold) {
            broadCastService.commitBlock(CommitBlock(
                    witnessSignature = CommitBlock.generateSignature(account.publicKey, account.privateKey!!, commitBlock.block),
                    witness = account.publicKey,
                    block = commitBlock.block
            ))
        }
    }

    fun commitBlockEvent(commitBlock: CommitBlock) {
        forkChoiceRuleService.checkSync()

        val blockProducerAtTime = blockProposerHandler.findBlockProducerAtTime(commitBlock.block.blockHeader.time).blockProducer
        if (blockProducerAtTime.accountId.address != commitBlock.block.blockProducerAddress.createAddress()) {
            logger.error("[COMMIT] [WRONG TIME] invalid block producer.")
            throw RuntimeException("[COMMIT] [WRONG TIME] invalid block producer.")
        }

        val blockProducerCurrent = blockProposerHandler.findBlockProducerAtTime(Instant.now().toEpochMilli()).blockProducer
        if (blockProducerCurrent.accountId.address != commitBlock.block.blockProducerAddress.createAddress()) {
            logger.error("[COMMIT] [TIMEOUT] invalid block producer.")
            throw RuntimeException("[COMMIT] [TIMEOUT] invalid block producer.")
        }

        if (!commitBlock.verifyWitnessSignature()) {
            logger.error("[COMMIT] [INVALID WITNESS SIGNATURE] invalid witness signature! byzantine witness : {}", commitBlock.witness.createAddress())
            throw RuntimeException("[COMMIT] [INVALID WITNESS SIGNATURE] invalid witness signature!")
        }

        if (validatorHandler.findValidator(commitBlock.witness.createAddress()) == null) {
            logger.error("[COMMIT] [INVALID WITNESS] invalid witness! byzantine witness : {}", commitBlock.witness.createAddress())
            throw RuntimeException("[COMMIT] [INVALID WITNESS] invalid witness signature!")
        }

        val lastBlock = databaseService.findLastBlock() ?: throw RuntimeException("last block is null")
        if (lastBlock.blockHash != commitBlock.block.blockHeader.prevBlockHash) {
            logger.error("[COMMIT] [INVALID BLOCK CHAIN] invalid block chain.")
            throw RuntimeException("[COMMIT] [INVALID BLOCK CHAIN] invalid block chain.")
        }

        databaseService.addCommitBlock(commitBlock)
        val validators = validatorHandler.findValidators()

        var commitWitnessCountThreshold = validators.size * 2 / 3
        if (commitWitnessCountThreshold < 3) {
            commitWitnessCountThreshold = validators.size
        }

        // consensus Commit!
        val commitWitnessCount = databaseService.findAllCommitBlock(commitBlock.block.blockHash).size
        logger.debug("[COMMIT] CommitWitnessCount : {}, CommitWitnessCountThreshold : {}", commitWitnessCount, commitWitnessCountThreshold)
        if (commitWitnessCount >= commitWitnessCountThreshold) {
            blockService.executeBlock(commitBlock.block)
        }
    }
}
