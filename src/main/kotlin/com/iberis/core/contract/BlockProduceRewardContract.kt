package com.iberis.core.contract

import com.google.protobuf.ByteString
import com.iberis.common.Coin
import com.iberis.common.ProtobufModel
import com.iberis.core.contract.executor.BlockProduceRewardContractHandler
import com.iberis.core.contract.executor.ContractHandler
import com.iberis.crypto.decodePublicKey
import com.iberis.db.DatabaseService
import com.iberis.util.createAddress
import java.security.PublicKey

/**
 * Please describe the role of the TransferContract
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-11
 *
 * @author namjug.kim
 * @since 2018-10-11
 * @version 0.1
 */
data class BlockProduceRewardContract(val from: PublicKey,
                                      val amount: Coin) : BaseContract(from, com.iberis.protocol.ContractProtocol.ContractType.BlockProduceRewardContract), ProtobufModel<com.iberis.protocol.ContractProtocol.PBlockProduceRewardContract> {
    companion object {
        fun parseFrom(rawData: ByteArray): BlockProduceRewardContract {
            val parsedData = com.iberis.protocol.ContractProtocol.PBlockProduceRewardContract.parseFrom(rawData)
            return parseFrom(parsedData)
        }

        fun parseFrom(fromModel: com.iberis.protocol.ContractProtocol.PBlockProduceRewardContract): BlockProduceRewardContract {
            return BlockProduceRewardContract(
                    from = fromModel.sender.toByteArray().decodePublicKey(),
                    amount = Coin(fromModel.amount)
            )
        }
    }

    override fun createHandler(databaseService: DatabaseService): ContractHandler {
        return BlockProduceRewardContractHandler(databaseService)
    }

    override fun rawData(): ByteArray {
        return toProtobuf().toByteArray()
    }

    override fun toProtobuf(): com.iberis.protocol.ContractProtocol.PBlockProduceRewardContract {
        return com.iberis.protocol.ContractProtocol.PBlockProduceRewardContract.newBuilder()
                .setSender(ByteString.copyFrom(from.encoded))
                .setAmount(amount.value)
                .build()
    }

    override fun toString(): String {
        return "from : " + from.createAddress() + " amount : " + amount.value
    }
}
