package com.iberis.common

/**
 * Please describe the role of the ProtobufModel
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-16
 *
 * @author namjug.kim
 * @since 2018-10-16
 * @version 0.1
 */
interface ProtobufModel<out T : com.google.protobuf.GeneratedMessageV3> {
    fun rawData(): ByteArray {
        return toProtobuf().toByteArray()
    }

    fun toProtobuf(): T
}
