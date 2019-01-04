package com.iberis.consensus.model

import com.google.protobuf.ByteString
import com.iberis.common.ProtobufModel
import com.iberis.core.block.Block
import com.iberis.crypto.decodePublicKey
import com.iberis.crypto.signing
import com.iberis.crypto.verifySigning
import com.iberis.protocol.Protocol
import java.security.PrivateKey
import java.security.PublicKey

/**
 * Please describe the role of the CommitBlock
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-16
 *
 * @author namjug.kim
 * @since 2018-10-16
 * @version 0.1
 */
class CommitBlock(val witnessSignature: ByteArray,
                  val witness: PublicKey,
                  val block: Block) : ProtobufModel<Protocol.PCommitBlock> {
    companion object {
        fun parseFrom(rawData: ByteArray): CommitBlock {
            val parsedData = Protocol.PCommitBlock.parseFrom(rawData)
            return parseFrom(parsedData)
        }

        fun parseFrom(fromModel: Protocol.PCommitBlock): CommitBlock {
            return CommitBlock(
                    witnessSignature = fromModel.witnessSignature.toByteArray(),
                    witness = fromModel.witnessAddress.toByteArray().decodePublicKey(),
                    block = Block.parseFrom(fromModel.block)
            )
        }

        fun generateSignature(witness: PublicKey, witnessPrivateKey: PrivateKey, block: Block): ByteArray {
            val data: ByteArray = witness.encoded + block.blockHash.bytes
            return witnessPrivateKey.signing(data)
        }
    }

    override fun toProtobuf(): Protocol.PCommitBlock {
        return Protocol.PCommitBlock.newBuilder()
                .setWitnessAddress(ByteString.copyFrom(witness.encoded))
                .setWitnessSignature(ByteString.copyFrom(witnessSignature))
                .setBlock(block.toProtobuf())
                .build()
    }

    fun verifyWitnessSignature(): Boolean {
        val data: ByteArray = witness.encoded + block.blockHash.bytes
        return witness.verifySigning(data, witnessSignature)
    }
}
