package com.iberis.consensus

import com.iberis.account.AccountService
import com.iberis.application.ApplicationProperties
import com.iberis.common.Coin
import com.iberis.common.logger
import com.iberis.core.block.Block
import com.iberis.core.block.BlockHeader
import com.iberis.core.contract.BlockProduceRewardContract
import com.iberis.core.transaction.Transaction
import com.iberis.core.transaction.TransactionBuilder
import com.iberis.core.transaction.TransactionService
import com.iberis.db.DatabaseService
import com.iberis.network.service.BroadCastService
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

/**
 * Please describe the role of the BlockProducerService
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-14
 *
 * @author namjug.kim
 * @since 2018-10-14
 * @version 0.1
 */
@Service
class BlockProducerService(private val forkChoiceRuleService: ForkChoiceRuleService,
                           private val blockProposerHandler: BlockProposerHandler,
                           private val transactionService: TransactionService,
                           private val broadCastService: BroadCastService,
                           private val accountService: AccountService,
                           private val databaseService: DatabaseService) {

    companion object {
        val logger = logger<BlockProducerService>()
        val lastBlockProduceIndex: AtomicLong = AtomicLong(-1)
    }

    fun produceBlock() {
        val account = ApplicationProperties.myAccount

        forkChoiceRuleService.checkSync()

        val currentBlockProduceRule = blockProposerHandler.findBlockProducerAtTime(Instant.now().toEpochMilli())
        val blockProduceIndex = currentBlockProduceRule.blockProposalIndex
        if (blockProduceIndex == lastBlockProduceIndex.getAndSet(blockProduceIndex)) {
            logger.debug("[PRODUCE BLOCK] already produce block")
            return
        }

        val currentBlockProducer = currentBlockProduceRule.blockProducer
        if (currentBlockProducer.accountId.address != account.accountId.address) {
            logger.debug("[PRODUCE BLOCK] not my turn. wrong index. current : {}, me : {}", currentBlockProducer.accountId.address, account.accountId.address)
            return
        }

        val coinbaseTransaction = TransactionBuilder(account.privateKey!!)
                .sender(account.publicKey)
                .contract(BlockProduceRewardContract(account.publicKey, Coin(1000)))
                .nonce(accountService.getNonce(account.accountId.address))
                .build()

        val transactions: List<Transaction> = listOf(coinbaseTransaction, *transactionService.findTransactionFromMempool(1000).toTypedArray())
        val lastBlock = databaseService.findLastBlock() ?: throw RuntimeException("last block is null")

        val blockHeader = BlockHeader(
                version = 1,
                prevBlockHash = lastBlock.blockHash,
                time = Instant.now().toEpochMilli(),
                merkleTreeHash = BlockHeader.buildMerkleTree(transactions)
        )

        val block = Block(
                blockHash = blockHeader.calculateBlockHash(),
                blockHeader = blockHeader,
                transactions = transactions,
                blockProducerAddress = account.publicKey,
                blockProducerSignature = Block.generateSignature(account.privateKey, account.publicKey, blockHeader.calculateBlockHash())
        )

        logger.info("\n=========== PRODUCE BLOCK ==============\n" +
                "\tBLOCK PRODUCER : ${account.accountId.address}\n" +
                "===============================================")

        broadCastService.produceBlock(block)
    }
}
