package com.iberis.explorer

import com.iberis.account.AccountService
import com.iberis.application.ApplicationProperties
import com.iberis.common.Coin
import com.iberis.core.contract.TransferContract
import com.iberis.core.transaction.TransactionBuilder
import com.iberis.core.transaction.TransactionService
import com.iberis.db.DatabaseService
import com.iberis.util.createAddress
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Please describe the role of the BlockExplorerController
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-17
 *
 * @author namjug.kim
 * @since 2018-10-17
 * @version 0.1
 */
@RestController
class BlockExplorerController(private val databaseService: DatabaseService,
                              private val transactionService: TransactionService,
                              private val accountService: AccountService) {

    @GetMapping("blocks")
    fun getBlocks(): List<Map<Any, Any>> {
        return databaseService.findAllBlock()
                .map { block ->
                    hashMapOf<Any, Any>(
                            "blockHash" to block.blockHash.hex,
                            "prevBlockHash" to block.blockHeader.prevBlockHash.hex,
                            "blockProducer" to block.blockProducerAddress.createAddress(),
                            "transactions" to block.transactions.map { transaction -> transaction.contractType.name + " : " + transaction.contract.toString() }
                    )
                }
    }

    @GetMapping("accounts")
    fun getAccounts(): List<String> {
        return databaseService.findAllAccount()
                .map { it.accountId.address + " : " + it.balance.value }
    }

    @GetMapping("wallet/transfer")
    fun transferTo(@RequestParam targetAddress: String, @RequestParam amount: Long) {
        val myAccount = ApplicationProperties.myAccount
        val findAccount = databaseService.findAccount(targetAddress)!!
        transactionService.requestTransaction(TransactionBuilder(myAccount.privateKey!!)
                .sender(myAccount.publicKey)
                .contract(TransferContract(myAccount.publicKey, findAccount.publicKey, Coin(amount)))
                .nonce(accountService.getNonce(myAccount.accountId.address))
                .build())
    }
}
