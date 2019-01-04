package com.iberis.core.block

import com.google.protobuf.ByteString
import com.iberis.common.ProtobufModel
import com.iberis.core.merkletree.MerkleTree
import com.iberis.core.transaction.Transaction
import com.iberis.crypto.Hash
import com.iberis.protocol.Protocol
import com.iberis.util.writeUint32
import java.io.ByteArrayOutputStream

/**
 * Please describe the role of the BlockHeader
 * <B>History:</B>
 * Created by namjug.kim on 2018. 9. 21.
 *
 * @author namjug.kim
 * @since 2018. 9. 21.
 * @version 0.1
 */
class BlockHeader(val version: Long,
                  var prevBlockHash: Hash,
                  val time: Long,
                  var merkleTreeHash: Hash) : ProtobufModel<Protocol.PBlockHeader> {

    companion object {
        fun buildMerkleTree(transactions: List<Transaction>): Hash {
            val transactionHashs = transactions
                    .map { it.transactionId }
                    .toList()

            return MerkleTree(transactionHashs).root()
        }

        fun parseFrom(rawData: ByteArray): BlockHeader {
            val parsedData = Protocol.PBlockHeader.parseFrom(rawData)
            return parseFrom(parsedData)
        }

        fun parseFrom(fromModel: Protocol.PBlockHeader): BlockHeader {
            return BlockHeader(
                    version = fromModel.version,
                    prevBlockHash = Hash.ByteWrapper(fromModel.prevBlockHash.toByteArray()).build(),
                    time = fromModel.time,
                    merkleTreeHash = Hash.ByteWrapper(fromModel.merkleTreeHash.toByteArray()).build()
            )
        }
    }

    override fun toProtobuf(): Protocol.PBlockHeader {
        return Protocol.PBlockHeader.newBuilder()
                .setMerkleTreeHash(ByteString.copyFrom(merkleTreeHash.bytes))
                .setPrevBlockHash(ByteString.copyFrom(prevBlockHash.bytes))
                .setTime(time)
                .setVersion(version)
                .build()
    }

    fun calculateBlockHash(): Hash {
        val outputStream = ByteArrayOutputStream()

        outputStream.writeUint32(version)
        outputStream.write(prevBlockHash.bytes.reversedArray())
        outputStream.write(merkleTreeHash.bytes.reversedArray())
        outputStream.writeUint32(time)

        return Hash(outputStream.toByteArray()).hashTwice()
    }


}
