package com.iberis.consensus.model

import com.google.protobuf.ByteString
import com.iberis.account.AccountId
import com.iberis.common.ProtobufModel
import com.iberis.protocol.Protocol

/**
 * Please describe the role of the BlockProducer
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-14
 *
 * @author namjug.kim
 * @since 2018-10-14
 * @version 0.1
 */
data class BlockProducer(val accountId: AccountId,
                         val host: String,
                         val port: Int) : ProtobufModel<Protocol.PBlockProducer> {
    companion object {
        fun parseFrom(fromModel: Protocol.PBlockProducer): BlockProducer {
            val host = fromModel.host
            val port = fromModel.port
            val accountId = AccountId.fromByteArray(fromModel.publicKey.toByteArray())

            return BlockProducer(accountId, host, port)
        }

        fun parseFrom(rawData: ByteArray): BlockProducer {
            val parseFrom = Protocol.PBlockProducer.parseFrom(rawData)
            return parseFrom(parseFrom)
        }
    }

    override fun toProtobuf(): Protocol.PBlockProducer {
        return Protocol.PBlockProducer.newBuilder()
                .setHost(host)
                .setPort(port)
                .setPublicKey(ByteString.copyFrom(accountId.publicKey.encoded))
                .build()
    }
}
