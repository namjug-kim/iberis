package com.iberis.core.transaction

import com.google.protobuf.ByteString
import com.iberis.common.ProtobufModel
import com.iberis.core.contract.AddBlockProducerContract
import com.iberis.core.contract.BlockProduceRewardContract
import com.iberis.core.contract.Contract
import com.iberis.core.contract.TransferContract
import com.iberis.crypto.Hash
import com.iberis.crypto.decodePublicKey
import com.iberis.crypto.signing
import com.iberis.crypto.verifySigning
import com.iberis.protocol.ContractProtocol
import com.iberis.protocol.Protocol
import com.iberis.util.writeUint32
import java.io.ByteArrayOutputStream
import java.security.PrivateKey
import java.security.PublicKey


/**
 * Please describe the role of the Transaction
 * <B>History:</B>
 * Created by namjug.kim on 2018. 9. 21.
 *
 * @author namjug.kim
 * @since 2018. 9. 21.
 * @version 0.1
 */
class Transaction(val nonce: Long,
                  val sender: PublicKey,
                  val signature: ByteArray,
                  val contractType: ContractProtocol.ContractType,
                  val contract: Contract) : ProtobufModel<Protocol.PTransaction> {

    val transactionId: Hash = calculateHash()

    companion object {
        fun generateSignature(senderPrivateKey: PrivateKey, senderPublicKey: PublicKey, contract: Contract): ByteArray {
            val data = senderPublicKey.encoded + contract.rawData()
            return senderPrivateKey.signing(data)
        }

        fun parseFrom(fromModel: Protocol.PTransaction): Transaction {
            return Transaction(
                    nonce = fromModel.nonce,
                    sender = fromModel.sender.toByteArray().decodePublicKey(),
                    signature = fromModel.signature.toByteArray(),
                    contractType = fromModel.contractType,
                    contract = parseContract(fromModel.contractType, fromModel.contract.toByteArray())
            )
        }

        fun parseFrom(rawData: ByteArray): Transaction {
            val parseFrom = Protocol.PTransaction.parseFrom(rawData)
            return parseFrom(parseFrom)
        }

        private fun parseContract(contractType: ContractProtocol.ContractType, rawData: ByteArray): Contract {
            return when (contractType) {
                ContractProtocol.ContractType.BlockProduceRewardContract -> BlockProduceRewardContract.parseFrom(rawData)
                ContractProtocol.ContractType.AddBlockProducerContract -> AddBlockProducerContract.parseFrom(rawData)
                ContractProtocol.ContractType.TransferContract -> TransferContract.parseFrom(rawData)
                else -> throw IllegalArgumentException("not implement contractType : $contractType")
            }
        }
    }

    override fun toProtobuf(): Protocol.PTransaction {
        return Protocol.PTransaction.newBuilder()
                .setNonce(nonce)
                .setSender(ByteString.copyFrom(sender.encoded))
                .setSignature(ByteString.copyFrom(signature))
                .setContractType(contract.contractType())
                .setContract(ByteString.copyFrom(contract.rawData()))
                .build()
    }

    fun verifySignature(): Boolean {
        val data = sender.encoded + contract.rawData()
        return sender.verifySigning(data, signature)
    }

    private fun calculateHash(): Hash {
        val outputStream = ByteArrayOutputStream()

        outputStream.writeUint32(nonce)
        outputStream.write(sender.encoded)
        outputStream.write(signature)
        outputStream.write(contract.rawData())

        return Hash(outputStream.toByteArray()).hashTwice()
    }

}
