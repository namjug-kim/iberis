package com.iberis.crypto

import java.security.*
import java.security.spec.X509EncodedKeySpec

/**
 * Please describe the role of the SigningCrypto
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-22
 *
 * @author namjug.kim
 * @since 2018-10-22
 * @version 0.1
 */
class SigningCrypto {
    companion object {
        fun generateKeyPair(spec: SigningCryptoSpec = SigningCryptoSpec.DEFAULT_SIGNATURE_SCHEME): KeyPair {
            val keyGen = KeyPairGenerator.getInstance(spec.algorithm, spec.algorithmProvider)
            keyGen.initialize(spec.ecSpec, spec.secureRandom)
            return keyGen.generateKeyPair()
        }
    }
}

fun ByteArray.decodePublicKey(spec: SigningCryptoSpec = SigningCryptoSpec.DEFAULT_SIGNATURE_SCHEME): PublicKey {
    val keyFactory = KeyFactory.getInstance(spec.algorithm, spec.algorithmProvider)
    return keyFactory.generatePublic(X509EncodedKeySpec(this))
}

fun PublicKey.encodePublicKey(): ByteArray {
    return this.encoded
}

fun PrivateKey.signing(data: ByteArray, spec: SigningCryptoSpec = SigningCryptoSpec.DEFAULT_SIGNATURE_SCHEME): ByteArray {
    val dsa: Signature = Signature.getInstance(spec.signatureName, spec.signatureProvider)
    dsa.initSign(this)
    dsa.update(data)
    return dsa.sign()
}

fun PublicKey.verifySigning(data: ByteArray, signature: ByteArray, spec: SigningCryptoSpec = SigningCryptoSpec.DEFAULT_SIGNATURE_SCHEME): Boolean {
    val ecdsaVerify = Signature.getInstance(spec.signatureName, spec.signatureProvider)
    ecdsaVerify.initVerify(this)
    ecdsaVerify.update(data)
    return ecdsaVerify.verify(signature)
}
