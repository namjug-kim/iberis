package com.iberis.core.contract.executor

import com.iberis.common.logger
import com.iberis.core.contract.Contract
import com.iberis.db.DatabaseService
import org.springframework.stereotype.Service

/**
 * Please describe the role of the ContractService
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-11
 *
 * @author namjug.kim
 * @since 2018-10-11
 * @version 0.1
 */
@Service
class ContractService(private val databaseService: DatabaseService) {
    companion object {
        val logger = logger<ContractService>()
    }

    fun executeContract(contract: Contract) {
        val contractHandler = contract.createHandler(databaseService)

        try {
            contractHandler.validate(contract)
            contractHandler.execute(contract)
        } catch (e: Exception) {
            logger.error("error occur execute contract")
        }
    }
}
