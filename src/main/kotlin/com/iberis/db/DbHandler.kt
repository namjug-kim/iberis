package com.iberis.db

import com.google.protobuf.GeneratedMessageV3
import com.iberis.common.ProtobufModel

/**
 * Please describe the role of the DbHandler
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-11
 *
 * @author namjug.kim
 * @since 2018-10-11
 * @version 0.1
 */
interface DbHandler<T : ProtobufModel<GeneratedMessageV3>> {
    fun get(key: String): T?

    fun get(limit: Long): List<T>

    fun getAll(): List<T>

    fun getFirst(): T?

    fun getLast(): T?

    fun put(key: String, value: T)

    fun remove(hex: String)

    fun deserialize(byteArray: ByteArray): T

    fun serialize(value: T): ByteArray

    fun getIndex(index: Int): T?
    fun putIbAbsent(key: String, value: T)
}
