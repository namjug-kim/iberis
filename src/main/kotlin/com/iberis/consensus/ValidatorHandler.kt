package com.iberis.consensus

import com.iberis.consensus.model.BlockValidator

/**
 * Please describe the role of the ValidatorHandler
 * <B>History:</B>
 * Created by namjug.kim on 2018-11-04
 *
 * @author namjug.kim
 * @since 2018-11-04
 * @version 0.1
 */
interface ValidatorHandler {
    fun findValidator(address: String): BlockValidator?
    fun findValidators(): List<BlockValidator>
}
