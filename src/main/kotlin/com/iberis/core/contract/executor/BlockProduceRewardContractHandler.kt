package com.iberis.core.contract.executor

import com.iberis.account.Account
import com.iberis.core.contract.BlockProduceRewardContract
import com.iberis.core.contract.Contract
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
class BlockProduceRewardContractHandler(databaseService: DatabaseService) : BaseContractHandler(databaseService) {
    override fun validate(contract: Contract): Boolean {
        return true
    }

    override fun execute(contract: Contract) {
        val blockProduceRewardContract = contract as BlockProduceRewardContract
        var senderAccount = databaseService.findAccount(blockProduceRewardContract.from.createAddress())
        if (senderAccount == null) {
            val account = Account.emptyAccount(blockProduceRewardContract.from)
            databaseService.saveAccount(account)
            senderAccount = account
        }

        senderAccount.balance += contract.amount

        databaseService.saveAccount(senderAccount)
    }
}
