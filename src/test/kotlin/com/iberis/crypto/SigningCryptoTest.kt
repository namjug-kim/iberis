package com.iberis.crypto

import com.iberis.util.createAddress
import org.junit.Assert.*
import org.junit.Test

/**
 * Please describe the role of the SigningCryptoTest
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-22
 *
 * @author namjug.kim
 * @version 0.1
 * @since 2018-10-22
 */
internal class SigningCryptoTest {
    @Test
    fun `decode encode publicKey`() {
        // given
        val generateKeyPair = SigningCrypto.generateKeyPair()
        val publicKey = generateKeyPair.public
        val encodedData = publicKey.encodePublicKey()

        // when
        val decodePublicKey = encodedData.decodePublicKey()

        // then
        assertEquals(publicKey.createAddress(), decodePublicKey.createAddress())
    }

    @Test
    fun `correct digital signing`() {
        // given
        val generateKeyPair = SigningCrypto.generateKeyPair()
        val publicKey = generateKeyPair.public
        val privateKey = generateKeyPair.private
        val givenData = "test data".toByteArray()

        // when
        val signing = privateKey.signing(givenData)
        val isVerifiedSigning = publicKey.verifySigning(givenData, signing)

        // then
        assertTrue(isVerifiedSigning)
    }

    @Test
    fun `wrong digital signing`() {
        // given
        val generateKeyPair = SigningCrypto.generateKeyPair()
        val publicKey = generateKeyPair.public
        val privateKey = generateKeyPair.private
        val givenData = "test data".toByteArray()

        // when
        val anotherPrivateKey = SigningCrypto.generateKeyPair().private
        val signing = anotherPrivateKey.signing(givenData)
        val isVerifiedSigning = publicKey.verifySigning(givenData, signing)

        // then
        assertFalse(isVerifiedSigning)
    }
}
