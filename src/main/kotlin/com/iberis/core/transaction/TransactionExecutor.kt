package com.iberis.core.transaction

import com.iberis.account.AccountService
import com.iberis.common.logger
import com.iberis.core.contract.executor.ContractService
import com.iberis.core.exception.TransactionInvalidException
import com.iberis.db.DatabaseService
import com.iberis.util.byteArrayToHex
import com.iberis.util.createAddress
import org.springframework.stereotype.Service

/**
 * Please describe the role of the TransactionExecutor
 * <B>History:</B>
 * Created by namjug.kim on 2018-11-07
 *
 * @author namjug.kim
 * @since 2018-11-07
 * @version 0.1
 */
@Service
class TransactionExecutor(private val accountService: AccountService,
                          private val databaseService: DatabaseService,
                          private val contractService: ContractService) {

    companion object {
        val logger = logger<TransactionExecutor>()
    }

    fun execute(transaction: Transaction) {
        val senderAddress = transaction.sender.createAddress()

        if (!transaction.verifySignature()) {
            throw TransactionInvalidException("transaction signature invalid")
        }

        val accountNonce = accountService.getNonce(senderAddress)
        val transactionNonce = transaction.nonce

        if (accountNonce != transactionNonce) {
            logger.error("transaction nonce invalid. accountNonce : $accountNonce, transactionNonce : $transactionNonce")
            throw TransactionInvalidException("transaction nonce invalid")
        }

        if (transaction.sender.encoded.byteArrayToHex() != transaction.contract.getOwner().encoded.byteArrayToHex()) {
            throw TransactionInvalidException("transaction contract owner invalid")
        }

        if (databaseService.findTransaction(transaction.transactionId.hex) != null) {
            throw TransactionInvalidException("transaction already executed")
        }

        contractService.executeContract(transaction.contract)
        databaseService.removeTransactionMemPool(transaction)
        databaseService.saveTransaction(transaction)
        accountService.increaseNonce(senderAddress)
    }

}
