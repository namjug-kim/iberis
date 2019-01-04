package com.iberis.network.service

import com.iberis.consensus.model.CommitBlock
import com.iberis.core.block.Block
import com.iberis.core.transaction.Transaction

/**
 * Please describe the role of the BroadCastService
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-19
 *
 * @author namjug.kim
 * @since 2018-10-19
 * @version 0.1
 */
interface BroadCastService {
    val PRODUCE_BLOCK_CHANNEL: String
        get() = "produce_block"
    val PRE_COMMIT_CHANNEL: String
        get() = "pre_commit"
    val COMMIT_CHANNEL: String
        get() = "commit"
    val REQUEST_TRANSACTION_CHANNEL: String
        get() = "request_transaction"

    fun addListener(topicName: String, consumer: (ByteArray) -> Unit)
    fun requestTransaction(transaction: Transaction)
    fun produceBlock(block: Block)
    fun preCommitBlock(commitBlock: CommitBlock)
    fun commitBlock(commitBlock: CommitBlock)
}
