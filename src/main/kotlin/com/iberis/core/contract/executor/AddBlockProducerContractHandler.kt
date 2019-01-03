package com.iberis.core.contract.executor

import com.iberis.account.Account
import com.iberis.account.AccountId
import com.iberis.consensus.model.BlockProducer
import com.iberis.core.contract.AddBlockProducerContract
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
class AddBlockProducerContractHandler(databaseService: DatabaseService) : BaseContractHandler(databaseService) {
    override fun validate(contract: Contract): Boolean {
        return true
    }

    override fun execute(contract: Contract) {
        val addBlockProducerContract = contract as AddBlockProducerContract

        val senderAccount = databaseService.findAccount(addBlockProducerContract.from.createAddress())
        if (senderAccount == null) {
            val account = Account.emptyAccount(addBlockProducerContract.from)
            databaseService.saveAccount(account)
        }

        // TODO block producer 선정 로직 변경
        val blockProducer = BlockProducer(AccountId(addBlockProducerContract.from), "127.0.0.1", 8080)
        databaseService.addBlockProducer(blockProducer)
    }
}
