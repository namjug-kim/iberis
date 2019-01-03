package com.iberis.util

import com.iberis.crypto.Hash
import java.security.PublicKey

/**
 * Please describe the role of the AccountUtil
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-12
 *
 * @author namjug.kim
 * @since 2018-10-12
 * @version 0.1
 */
fun PublicKey.createAddress(): String {
    return Hash(this.encoded).hash().hex
}
