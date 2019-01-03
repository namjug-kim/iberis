package com.iberis.application

import com.iberis.account.Account
import com.iberis.db.DatabaseService
import org.springframework.stereotype.Service

/**
 * Please describe the role of the ApplicationProperties
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-16
 *
 * @author namjug.kim
 * @since 2018-10-16
 * @version 0.1
 */
@Service
class ApplicationProperties(databaseService: DatabaseService) {
    companion object {
        var myAccount: Account = Account.createAccount()
        var blockProduceInterval: Long = 2 * 1000
    }

    init {
        databaseService.saveAccount(myAccount)
    }
}
