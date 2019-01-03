package com.iberis.account

import com.iberis.util.createAddress
import java.io.Serializable
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec


/**
 * Please describe the role of the AccountId
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-14
 *
 * @author namjug.kim
 * @since 2018-10-14
 * @version 0.1
 */
data class AccountId(@Transient val publicKey: PublicKey) : Serializable {
    val address: String = publicKey.createAddress()

    companion object {
        fun fromByteArray(byteArray: ByteArray): AccountId {
            val keyFactory = KeyFactory.getInstance("ECDSA", "BC")
            val x509publicKey = X509EncodedKeySpec(byteArray)
            return AccountId(keyFactory.generatePublic(x509publicKey))
        }
    }
}
