package com.iberis.crypto

import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.SecureRandom
import java.security.Security
import java.security.spec.AlgorithmParameterSpec

/**
 * Please describe the role of the SigningCryptoSpec
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-22
 *
 * @author namjug.kim
 * @since 2018-10-22
 * @version 0.1
 */
class SigningCryptoSpec(val algorithm: String,
                        val algorithmProvider: String,
                        val signatureName: String,
                        val signatureProvider: String,
                        val secureRandom: SecureRandom,
                        val ecSpec: AlgorithmParameterSpec) {

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    companion object {
        val DEFAULT_SIGNATURE_SCHEME = SigningCryptoSpec(
                algorithm = "ECDSA",
                algorithmProvider = "BC",
                signatureName = "SHA256withECDSA",
                signatureProvider = "BC",
                secureRandom = SecureRandom(),
                ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1")
        )
    }
}
