package com.iberis.crypto

import com.iberis.util.byteArrayToHex
import com.iberis.util.hexToByteArray
import java.security.MessageDigest

/**
 * Please describe the role of the Hash
 * <B>History:</B>
 * Created by namjug.kim on 2018. 9. 21.
 *
 * @author namjug.kim
 * @since 2018. 9. 21.
 * @version 0.1
 */
class Hash(bytes: ByteArray) {
    @Transient
    private val hashAlgorithm: MessageDigest = MessageDigest.getInstance("SHA-256")

    var hex = bytes.byteArrayToHex()
        private set
    var bytes = bytes
        set(value) {
            field = value
            this.hex = value.byteArrayToHex()
        }

    class HexWrapper(private val hex: String) {
        fun build() = Hash(hex.hexToByteArray())
    }

    class ByteWrapper(private val bytes: ByteArray) {
        fun build() = Hash(bytes)
    }

    fun concatHash(other: Hash): Hash {
        val thisHashByteArray = this.bytes.reversedArray()
        val otherHashByteArray = other.bytes.reversedArray()
        this.bytes = hashTwiceReversed(thisHashByteArray + otherHashByteArray)
        return this
    }

    fun hashTwice(): Hash {
        this.bytes = hashTwiceReversed(this.bytes)
        return this
    }

    fun hashTwiceReversed(): Hash {
        this.bytes = hashTwiceReversed(this.bytes).reversedArray()
        return this
    }

    fun hash(): Hash {
        this.bytes = hash(bytes)
        return this
    }

    private fun hash(bytes: ByteArray): ByteArray {
        return hashAlgorithm.digest(bytes)
    }

    private fun hashTwiceReversed(bytes: ByteArray) = hash(hash(bytes)).reversedArray()

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other == null || javaClass != other.javaClass -> false
            else -> {
                val otherValue = other as Hash
                this.hex == otherValue.hex
            }
        }
    }

    override fun hashCode(): Int {
        return hex.hashCode()
    }

    override fun toString(): String {
        return this.hex
    }
}
