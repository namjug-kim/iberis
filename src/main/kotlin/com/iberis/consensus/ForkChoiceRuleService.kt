package com.iberis.consensus

import com.iberis.application.ApplicationProperties
import com.iberis.core.block.BlockService
import com.iberis.db.DatabaseService
import com.iberis.node.client.BlockGrpcClientService
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Please describe the role of the ForkChoiceRuleService
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-31
 *
 * @author namjug.kim
 * @since 2018-10-31
 * @version 0.1
 */
@Service
class ForkChoiceRuleService(private val databaseService: DatabaseService,
                            private val blockGrpcClientService: BlockGrpcClientService,
                            private val blockProposalHandler: BlockProposerHandler,
                            private val blockService: BlockService) {
    fun checkSync(): Boolean {
        val lastBlock = databaseService.findLastBlock() ?: throw RuntimeException("last block is null")
        val lastBlockProduceRule = blockProposalHandler.findBlockProducerAtTime(lastBlock.blockHeader.time)
        if (lastBlockProduceRule.roundEndTime > Instant.now().toEpochMilli() - ApplicationProperties.blockProduceInterval) {
            return true
        }

        while (true) {
            try {
                val currentHeight = blockService.currentHeight()
                val block = blockGrpcClientService.getBlock(currentHeight)
                blockService.executeBlock(block)
            } catch (e: Exception) {
                return true
            }
        }
    }

}
