package com.iberis.core.contract.executor

import com.iberis.core.contract.Contract

/**
 * Please describe the role of the ContractHandler
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-12
 *
 * @author namjug.kim
 * @since 2018-10-12
 * @version 0.1
 */
interface ContractHandler {
    fun validate(contract: Contract): Boolean

    fun execute(contract: Contract)
}
