package com.iberis.core.contract.executor

import com.iberis.account.Account
import com.iberis.core.contract.Contract
import com.iberis.core.contract.TransferContract
import com.iberis.db.DatabaseService
import com.iberis.util.createAddress

/**
 * Please describe the role of the TransferContractHandler
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-11
 *
 * @author namjug.kim
 * @since 2018-10-11
 * @version 0.1
 */
class TransferContractHandler(databaseService: DatabaseService) : BaseContractHandler(databaseService) {
    override fun validate(contract: Contract): Boolean {
        return true
    }

    override fun execute(contract: Contract) {
        val transferContract = contract as TransferContract
        var senderAccount = databaseService.findAccount(transferContract.from.createAddress())
        if (senderAccount == null) {
            val account = Account.emptyAccount(transferContract.from)
            databaseService.saveAccount(account)
            senderAccount = account
        }


        var receiverAccount = databaseService.findAccount(transferContract.to.createAddress())
        if (receiverAccount == null) {
            val account = Account.emptyAccount(transferContract.to)
            databaseService.saveAccount(account)
            receiverAccount = account
        }

        if (senderAccount.balance < contract.amount) {
            throw RuntimeException("")
        }

        senderAccount.balance -= contract.amount
        receiverAccount.balance += contract.amount
        databaseService.saveAccount(senderAccount)
        databaseService.saveAccount(receiverAccount)
    }

}
