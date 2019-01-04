package com.iberis.core.block

import com.google.protobuf.ByteString
import com.iberis.common.ProtobufModel
import com.iberis.core.transaction.Transaction
import com.iberis.crypto.Hash
import com.iberis.crypto.decodePublicKey
import com.iberis.crypto.signing
import com.iberis.crypto.verifySigning
import com.iberis.protocol.Protocol
import java.security.PrivateKey
import java.security.PublicKey

/**
 * Please describe the role of the Block
 * <B>History:</B>
 * Created by namjug.kim on 2018. 9. 21.
 *
 * @author namjug.kim
 * @since 2018. 9. 21.
 * @version 0.1
 */
data class Block(val blockHeader: BlockHeader,
                 val blockHash: Hash = blockHeader.calculateBlockHash(),
                 val transactions: List<Transaction>,
                 val blockProducerAddress: PublicKey,
                 val blockProducerSignature: ByteArray) : ProtobufModel<Protocol.PBlock> {

    companion object {
        fun parseFrom(rawData: ByteArray): Block {
            val parsedData = Protocol.PBlock.parseFrom(rawData)
            return parseFrom(parsedData)
        }

        fun parseFrom(fromModel: Protocol.PBlock): Block {
            return Block(
                    blockHash = Hash.ByteWrapper(fromModel.blockHash.toByteArray()).build(),
                    blockHeader = BlockHeader.parseFrom(fromModel.blockHeader),
                    transactions = fromModel.transactionsList.map { Transaction.parseFrom(it) },
                    blockProducerAddress = fromModel.blockProducerAddress.toByteArray().decodePublicKey(),
                    blockProducerSignature = fromModel.blockProducerSignature.toByteArray()
            )
        }

        fun generateSignature(blockProducerPrivateKey: PrivateKey, blockProducerPublicKey: PublicKey, blockHash: Hash): ByteArray {
            val data = blockProducerPublicKey.encoded + blockHash.bytes
            return blockProducerPrivateKey.signing(data)
        }
    }

    override fun toProtobuf(): Protocol.PBlock {
        return Protocol.PBlock.newBuilder()
                .setBlockProducerAddress(ByteString.copyFrom(blockProducerAddress.encoded))
                .setBlockProducerSignature(ByteString.copyFrom(blockProducerSignature))
                .setBlockHash(ByteString.copyFrom(blockHash.bytes))
                .addAllTransactions(transactions.map { it.toProtobuf() })
                .setBlockHeader(blockHeader.toProtobuf())
                .build()
    }

    fun verifySignature(): Boolean {
        val data = blockProducerAddress.encoded + blockHash.bytes
        return blockProducerAddress.verifySigning(data, blockProducerSignature)
    }
}
