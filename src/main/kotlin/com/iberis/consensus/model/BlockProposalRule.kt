package com.iberis.consensus.model

/**
 * Please describe the role of the BlockProposalRule
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-17
 *
 * @author namjug.kim
 * @since 2018-10-17
 * @version 0.1
 */
data class BlockProposalRule(val blockProducer: BlockProducer,
                             val roundStartTime: Long,
                             val roundEndTime: Long,
                             val blockProposalIndex: Long)
