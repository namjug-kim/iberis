package com.iberis.core.transaction

import com.iberis.account.Account
import com.iberis.account.AccountService
import com.iberis.common.Coin
import com.iberis.core.contract.TransferContract
import com.iberis.core.contract.executor.ContractService
import com.iberis.db.DatabaseService
import com.iberis.node.service.BroadCastService
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.mockito.Mockito

/**
 * Please describe the role of the TransactionServiceTest
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-12
 *
 * @author namjug.kim
 * @version 0.1
 * @since 2018-10-12
 */
class TransactionServiceTest {
    private val dataBaseService: DatabaseService = DatabaseService()

    private val accountService: AccountService = AccountService(dataBaseService)

    private val contractService: ContractService = ContractService(dataBaseService)

    private val transactionExecutor: TransactionExecutor = TransactionExecutor(accountService, dataBaseService, contractService)

    private val transactionService: TransactionService = TransactionService(dataBaseService, Mockito.mock(BroadCastService::class.java), transactionExecutor)

    @Test
    fun `test transfer transaction`() {
        // given
        var accountA = Account.createAccount()
        accountA.balance = Coin(100)
        dataBaseService.saveAccount(accountA)

        var accountB = Account.createAccount()
        accountB.balance = Coin(0)
        dataBaseService.saveAccount(accountB)

        // when
        // transfer 30 coin A to B
        val transaction = TransactionBuilder(accountA.privateKey!!)
                .sender(accountA.publicKey)
                .nonce(0)
                .contract(TransferContract(accountA.publicKey, accountB.publicKey, Coin(30)))
                .build()
        transactionService.executeTransaction(transaction)

        // then
        accountA = checkNotNull(dataBaseService.findAccount(accountA.accountId.address))
        accountB = checkNotNull(dataBaseService.findAccount(accountB.accountId.address))
        assertEquals(Coin(70), accountA.balance)
        assertEquals(Coin(30), accountB.balance)
    }

    @Test(expected = com.iberis.core.exception.TransactionInvalidException::class)
    fun `test wrong transaction sign`() {
        // given
        val accountA = Account.createAccount()
        accountA.balance = com.iberis.common.Coin(100)
        dataBaseService.saveAccount(accountA)

        val accountB = Account.createAccount()
        accountB.balance = Coin(0)
        dataBaseService.saveAccount(accountB)

        // when
        // create transaction sender 'accountA' using 'accountB' private key
        val transaction = TransactionBuilder(accountB.privateKey!!)
                .sender(accountA.publicKey)
                .nonce(0)
                .contract(TransferContract(accountA.publicKey, accountB.publicKey, Coin(30)))
                .build()
        transactionService.executeTransaction(transaction)

        // then
        fail("occur exception when using wrong private key")
    }
}
