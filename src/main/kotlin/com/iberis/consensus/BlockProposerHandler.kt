package com.iberis.consensus

import com.iberis.consensus.model.BlockProducer
import com.iberis.consensus.model.BlockProposalRule

/**
 * Please describe the role of the BlockProposerHandler
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-22
 *
 * @author namjug.kim
 * @version 0.1
 * @since 2018-10-22
 */
interface BlockProposerHandler {
    fun findActiveBlockProducers(): List<BlockProducer>

    fun findBlockProducerAtTime(time: Long): BlockProposalRule
}
