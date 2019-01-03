package com.iberis.util

import java.io.ByteArrayOutputStream
import javax.xml.bind.DatatypeConverter

/**
 * Please describe the role of the Hex
 * <B>History:</B>
 * Created by namjug.kim on 2018. 9. 21.
 *
 * @author namjug.kim
 * @since 2018. 9. 21.
 * @version 0.1
 */
fun String.hexToByteArray(): ByteArray = DatatypeConverter.parseHexBinary(this)

fun ByteArray.byteArrayToHex(): String = DatatypeConverter.printHexBinary(this)

/**
 * Write 4 bytes to the output stream as unsigned 32-bit integer in little endian format.
 */
fun ByteArrayOutputStream.writeUint32(value: Long) {
    val littleEndian = 0xFF.toLong()
    this.write((littleEndian and value).toInt())
    this.write((littleEndian and (value shr 8)).toInt())
    this.write((littleEndian and (value shr 16)).toInt())
    this.write((littleEndian and (value shr 24)).toInt())
}

