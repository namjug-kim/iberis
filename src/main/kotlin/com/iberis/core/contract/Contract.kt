package com.iberis.core.contract

import com.iberis.core.contract.executor.ContractHandler
import com.iberis.db.DatabaseService
import com.iberis.protocol.ContractProtocol
import java.security.PublicKey

/**
 * Please describe the role of the Contract
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-12
 *
 * @author namjug.kim
 * @since 2018-10-12
 * @version 0.1
 */
interface Contract {
    fun createHandler(databaseService: DatabaseService): ContractHandler

    fun getOwner(): PublicKey

    fun rawData(): ByteArray

    fun contractType(): ContractProtocol.ContractType
}
