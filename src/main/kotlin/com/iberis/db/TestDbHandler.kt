package com.iberis.db

import com.google.protobuf.GeneratedMessageV3
import com.iberis.common.ProtobufModel
import java.util.*
import kotlin.streams.toList

class TestDbHandler<T : ProtobufModel<GeneratedMessageV3>>(
        val deserializeFunction: (ByteArray) -> T,
        val serializeFunction: (T) -> ByteArray) : DbHandler<T> {

    private val cache: MutableMap<String, ByteArray> = Collections.synchronizedMap(LinkedHashMap())

    override fun deserialize(byteArray: ByteArray): T {
        return deserializeFunction(byteArray)
    }

    override fun serialize(value: T): ByteArray {
        return serializeFunction(value)
    }

    override fun getIndex(index: Int): T? {
        return when {
            cache.isEmpty() -> null
            cache.size < index -> null
            else -> {
                val lastElement = cache.entries.toTypedArray()[index].value
                deserialize(lastElement)
            }
        }
    }

    override fun getFirst(): T? {
        return when {
            cache.isEmpty() -> null
            else -> {
                val lastElement = cache.entries.toTypedArray()[0].value
                deserialize(lastElement)
            }
        }
    }

    override fun get(limit: Long): List<T> {
        return cache.entries
                .stream()
                .limit(limit)
                .map { it.value }
                .map { deserialize(it) }
                .toList()
    }

    override fun getLast(): T? {
        return when {
            cache.isEmpty() -> null
            else -> {
                val lastElement = cache.entries.toTypedArray()[cache.size - 1].value
                deserialize(lastElement)
            }
        }
    }

    override fun get(key: String): T? {
        val value = cache[key] ?: return null
        return deserialize(value)
    }

    override fun putIbAbsent(key: String, value: T) {
        cache.putIfAbsent(key, value.rawData())
    }

    override fun put(key: String, value: T) {
        cache.put(key, value.rawData())
    }

    override fun getAll(): List<T> {
        return cache.values
                .map { deserialize(it) }
    }

    override fun remove(hex: String) {
        cache.remove(hex)
    }
}
