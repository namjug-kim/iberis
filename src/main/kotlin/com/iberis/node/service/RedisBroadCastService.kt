package com.iberis.node.service

import com.iberis.consensus.model.CommitBlock
import com.iberis.core.block.Block
import com.iberis.core.transaction.Transaction
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.client.codec.ByteArrayCodec
import org.springframework.stereotype.Service

/**
 * Please describe the role of the RedisBroadCastService
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-16
 *
 * @author namjug.kim
 * @since 2018-10-16
 * @version 0.1
 */
@Service
class RedisBroadCastService : BroadCastService {
    private val redissonClient: RedissonClient = createRedisson()

    private fun createRedisson(): RedissonClient {
        return Redisson.create()
    }

    override fun addListener(topicName: String, consumer: (ByteArray) -> Unit) {
        redissonClient.getTopic<ByteArray>(topicName, ByteArrayCodec.INSTANCE)
                .addListener { _, msg -> consumer(msg) }
    }

    override fun requestTransaction(transaction: Transaction) {
        redissonClient.getTopic<ByteArray>(REQUEST_TRANSACTION_CHANNEL, ByteArrayCodec.INSTANCE)
                .publish(transaction.rawData())
    }

    override fun produceBlock(block: Block) {
        redissonClient.getTopic<ByteArray>(PRODUCE_BLOCK_CHANNEL, ByteArrayCodec.INSTANCE)
                .publish(block.rawData())
    }

    override fun preCommitBlock(commitBlock: CommitBlock) {
        redissonClient.getTopic<ByteArray>(PRE_COMMIT_CHANNEL, ByteArrayCodec.INSTANCE)
                .publish(commitBlock.rawData())
    }

    override fun commitBlock(commitBlock: CommitBlock) {
        redissonClient.getTopic<ByteArray>(COMMIT_CHANNEL, ByteArrayCodec.INSTANCE)
                .publish(commitBlock.rawData())
    }
}
