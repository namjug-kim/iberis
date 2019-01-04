package com.iberis.core.contract

import com.google.protobuf.ByteString
import com.iberis.common.ProtobufModel
import com.iberis.core.contract.executor.AddBlockProducerContractHandler
import com.iberis.core.contract.executor.ContractHandler
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
data class AddBlockProducerContract(val from: PublicKey) : BaseContract(from, ContractProtocol.ContractType.AddBlockProducerContract), ProtobufModel<ContractProtocol.PAddBlockProducerContract> {
    companion object {
        fun parseFrom(rawData: ByteArray): AddBlockProducerContract {
            val parsedData = ContractProtocol.PAddBlockProducerContract.parseFrom(rawData)
            return parseFrom(parsedData)
        }

        fun parseFrom(fromModel: ContractProtocol.PAddBlockProducerContract): AddBlockProducerContract {
            return AddBlockProducerContract(
                    from = fromModel.sender.toByteArray().decodePublicKey()
            )
        }
    }

    override fun createHandler(databaseService: DatabaseService): ContractHandler {
        return AddBlockProducerContractHandler(databaseService)
    }

    override fun rawData(): ByteArray {
        return toProtobuf().toByteArray()
    }

    override fun toProtobuf(): ContractProtocol.PAddBlockProducerContract {
        return ContractProtocol.PAddBlockProducerContract.newBuilder()
                .setSender(ByteString.copyFrom(from.encoded))
                .build()
    }

    override fun toString(): String {
        return "from : " + from.createAddress()
    }
}
