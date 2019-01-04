package com.iberis.account

import com.google.protobuf.ByteString
import com.iberis.common.Coin
import com.iberis.common.ProtobufModel
import com.iberis.crypto.SigningCrypto
import com.iberis.crypto.decodePublicKey
import com.iberis.protocol.Protocol
import java.security.PrivateKey
import java.security.PublicKey


/**
 * Please describe the role of the Account
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-11
 *
 * @author namjug.kim
 * @since 2018-10-11
 * @version 0.1
 */
data class Account(val privateKey: PrivateKey?,
                   val publicKey: PublicKey,
                   var nonce: Long,
                   var balance: Coin) : ProtobufModel<Protocol.PAccount> {
    val accountId: AccountId = AccountId(publicKey)

    companion object {
        fun createAccount(): Account {
            val keyPair = SigningCrypto.generateKeyPair()
            val privateKey = keyPair.private
            val publicKey = keyPair.public
            return Account(privateKey, publicKey, 0, Coin(0))
        }

        fun emptyAccount(publicKey: PublicKey): Account {
            return Account(null, publicKey, 0, Coin(0))
        }

        fun parseFrom(fromModel: Protocol.PAccount): Account {
            return Account(
                    privateKey = null,
                    publicKey = fromModel.publicKey.toByteArray().decodePublicKey(),
                    balance = Coin(fromModel.balance),
                    nonce = fromModel.nonce
            )
        }

        fun parseFrom(rawData: ByteArray): Account {
            val parseFrom = Protocol.PAccount.parseFrom(rawData)
            return parseFrom(parseFrom)
        }
    }

    override fun toProtobuf(): Protocol.PAccount {
        return Protocol.PAccount.newBuilder()
                .setPublicKey(ByteString.copyFrom(publicKey.encoded))
                .setBalance(balance.value)
                .setNonce(nonce)
                .build()
    }
}
