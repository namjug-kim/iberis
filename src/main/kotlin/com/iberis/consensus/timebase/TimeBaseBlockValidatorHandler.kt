package com.iberis.consensus.timebase

import com.iberis.consensus.ValidatorHandler
import com.iberis.consensus.model.BlockValidator
import com.iberis.db.DatabaseService
import org.springframework.stereotype.Component

/**
 * Please describe the role of the TimeBaseBlockValidatorHandler
 * <B>History:</B>
 * Created by namjug.kim on 2018-11-04
 *
 * @author namjug.kim
 * @since 2018-11-04
 * @version 0.1
 */
@Component
class TimeBaseBlockValidatorHandler(private val databaseService: DatabaseService) : ValidatorHandler {
    override fun findValidators(): List<BlockValidator> {
        return databaseService.findBlockProducers()
                .map { BlockValidator(it.accountId.address) }
    }

    override
    fun findValidator(address: String): BlockValidator? {
        return databaseService.findBlockProducer(address)
                ?.let { BlockValidator(it.accountId.address) }
    }
}
