package com.iberis.core.contract.executor

import com.iberis.db.DatabaseService

/**
 * Please describe the role of the BaseContractHandler
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-12
 *
 * @author namjug.kim
 * @since 2018-10-12
 * @version 0.1
 */
abstract class BaseContractHandler(databaseService: DatabaseService) : ContractHandler {
    protected val databaseService: DatabaseService = databaseService
}
