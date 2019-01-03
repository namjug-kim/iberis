package com.iberis.consensus

import com.iberis.common.logger
import com.iberis.consensus.model.CommitBlock
import com.iberis.core.block.Block
import com.iberis.node.service.BroadCastService
import org.springframework.stereotype.Service

/**
 * Please describe the role of the ValidatorListener
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-22
 *
 * @author namjug.kim
 * @since 2018-10-22
 * @version 0.1
 */
@Service
class ValidatorListener(private val validatorService: ValidatorService,
                        broadCastService: BroadCastService) {

    companion object {
        val logger = logger<ValidatorListener>()
    }

    init {
        broadCastService.addListener(broadCastService.PRODUCE_BLOCK_CHANNEL) {
            try {
                validatorService.proposalBlockEvent(Block.parseFrom(it))
            } catch (e: Exception) {
            }
        }

        broadCastService.addListener(broadCastService.PRE_COMMIT_CHANNEL) {
            try {
                validatorService.preCommitBlockEvent(CommitBlock.parseFrom(it))
            } catch (e: Exception) {
                logger.error(e.message, e)
            }
        }


        broadCastService.addListener(broadCastService.COMMIT_CHANNEL) {
            try {
                validatorService.commitBlockEvent(CommitBlock.parseFrom(it))
            } catch (e: Exception) {
                logger.error(e.message, e)
            }
        }
    }
}
