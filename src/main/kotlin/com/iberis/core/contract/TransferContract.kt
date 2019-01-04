package com.iberis.core.contract

import com.google.protobuf.ByteString
import com.iberis.common.Coin
import com.iberis.common.ProtobufModel
import com.iberis.core.contract.executor.ContractHandler
import com.iberis.core.contract.executor.TransferContractHandler
import com.iberis.crypto.decodePublicKey
import com.iberis.db.DatabaseService
import com.iberis.protocol.ContractProtocol
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
data class TransferContract(val from: PublicKey,
                            val to: PublicKey,
                            val amount: Coin) : BaseContract(from, ContractProtocol.ContractType.TransferContract), ProtobufModel<ContractProtocol.PTransferContract> {
    companion object {
        fun parseFrom(rawData: ByteArray): TransferContract {
            val parsedData = ContractProtocol.PTransferContract.parseFrom(rawData)
            return parseFrom(parsedData)
        }

        fun parseFrom(fromModel: ContractProtocol.PTransferContract): TransferContract {
            return TransferContract(
                    from = fromModel.sender.toByteArray().decodePublicKey(),
                    to = fromModel.to.toByteArray().decodePublicKey(),
                    amount = Coin(fromModel.amount)
            )
        }
    }

    override fun createHandler(databaseService: DatabaseService): ContractHandler {
        return TransferContractHandler(databaseService)
    }

    override fun rawData(): ByteArray {
        return toProtobuf().toByteArray()
    }

    override fun toProtobuf(): ContractProtocol.PTransferContract {
        return ContractProtocol.PTransferContract.newBuilder()
                .setSender(ByteString.copyFrom(from.encoded))
                .setTo(ByteString.copyFrom(to.encoded))
                .setAmount(amount.value)
                .build()
    }

    override fun toString(): String {
        return "from : " + from.createAddress() + " to : " + to.createAddress() + " amount : " + amount.value
    }
}
