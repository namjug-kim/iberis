package com.iberis.consensus.timebase

import com.iberis.application.ApplicationProperties
import com.iberis.consensus.BlockProposerHandler
import com.iberis.consensus.model.BlockProducer
import com.iberis.consensus.model.BlockProposalRule
import com.iberis.db.DatabaseService
import org.springframework.stereotype.Service

/**
 * Please describe the role of the TimeBaseBlockProposerHandler
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-15
 *
 * @author namjug.kim
 * @since 2018-10-15
 * @version 0.1
 */
@Service
class TimeBaseBlockProposerHandler(val databaseService: DatabaseService) : BlockProposerHandler {

    override fun findActiveBlockProducers(): List<BlockProducer> {
        return databaseService.findBlockProducers()
                .sortedBy { it.accountId.address }
    }

    override fun findBlockProducerAtTime(time: Long): BlockProposalRule {
        val genesisBlock = databaseService.findFirstBlock() ?: throw RuntimeException("genesis block is null")
        val genesisBlockTime = genesisBlock.blockHeader.time

        val activeBlockProducers = findActiveBlockProducers()
        val blockProposalIndex = (time - genesisBlockTime) / (ApplicationProperties.blockProduceInterval)
        val currentIndex = blockProposalIndex % activeBlockProducers.size

        val roundStartTime = genesisBlockTime + blockProposalIndex * ApplicationProperties.blockProduceInterval

        return BlockProposalRule(
                blockProducer = activeBlockProducers[currentIndex.toInt()],
                blockProposalIndex = blockProposalIndex,
                roundStartTime = roundStartTime,
                roundEndTime = roundStartTime + ApplicationProperties.blockProduceInterval
        )
    }
}
