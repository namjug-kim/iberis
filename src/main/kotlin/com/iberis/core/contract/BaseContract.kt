package com.iberis.core.contract

import java.security.PublicKey

/**
 * Please describe the role of the BaseContract
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-11
 *
 * @author namjug.kim
 * @since 2018-10-11
 * @version 0.1
 */
abstract class BaseContract(val sender: PublicKey, private val contractType: com.iberis.protocol.ContractProtocol.ContractType) : Contract {
    final override fun getOwner(): PublicKey {
        return sender
    }

    override fun contractType(): com.iberis.protocol.ContractProtocol.ContractType {
        return contractType
    }
}
