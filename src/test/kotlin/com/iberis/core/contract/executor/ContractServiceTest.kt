package com.iberis.core.contract.executor

import com.iberis.account.Account
import com.iberis.common.Coin
import com.iberis.core.contract.TransferContract
import com.iberis.db.DatabaseService
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Please describe the role of the ContractServiceTest
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-12
 *
 * @author namjug.kim
 * @version 0.1
 * @since 2018-10-12
 */
class ContractServiceTest {

    val databaseService = DatabaseService()

    val contractService: ContractService = ContractService(databaseService)

    @Test
    fun `test transfer contract`() {
        // given
        var accountA = Account.createAccount()
        accountA.balance = Coin(100)
        databaseService.saveAccount(accountA)

        var accountB = Account.createAccount()
        accountB.balance = Coin(0)
        databaseService.saveAccount(accountB)

        // when
        val transferContract = TransferContract(accountA.publicKey, accountB.publicKey, Coin(30))
        contractService.executeContract(transferContract)

        // then
        accountA = checkNotNull(databaseService.findAccount(accountA.accountId.address))
        accountB = checkNotNull(databaseService.findAccount(accountB.accountId.address))
        assertEquals(Coin(70), accountA.balance)
        assertEquals(Coin(30), accountB.balance)
    }
}
