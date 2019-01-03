package com.iberis.account

import com.iberis.db.DatabaseService
import org.springframework.stereotype.Service

/**
 * Please describe the role of the AccountService
 * <B>History:</B>
 * Created by namjug.kim on 2018-11-07
 *
 * @author namjug.kim
 * @since 2018-11-07
 * @version 0.1
 */
@Service
class AccountService(private val databaseService: DatabaseService) {
    fun getNonce(address: String): Long {
        val nonce = databaseService.findAccount(address)?.nonce
        return nonce ?: 0
    }

    fun increaseNonce(address: String) {
        val findAccount = checkNotNull(databaseService.findAccount(address))
        findAccount.nonce += 1
        databaseService.saveAccount(findAccount)
    }
}
