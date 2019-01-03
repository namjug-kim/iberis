package com.iberis.common

import java.io.Serializable

/**
 * Please describe the role of the Coin
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-12
 *
 * @author namjug.kim
 * @since 2018-10-12
 * @version 0.1
 */
data class Coin(val value: Long) : Comparable<Coin>, Serializable {
    override fun compareTo(other: Coin): Int {
        return this.value.compareTo(other.value)
    }

    operator fun plus(other: Coin): Coin {
        return Coin(value + other.value)
    }

    operator fun minus(other: Coin): Coin {
        return Coin(value - other.value)
    }
}
