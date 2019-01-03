package com.iberis.core.transaction

import com.iberis.common.logger
import com.iberis.consensus.BlockProducerService
import com.iberis.db.DatabaseService
import com.iberis.node.service.BroadCastService
import org.springframework.stereotype.Component

/**
 * Please describe the role of the TransactionService
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-11
 *
 * @author namjug.kim
 * @since 2018-10-11
 * @version 0.1
 */
@Component
class TransactionService(private val databaseService: DatabaseService,
                         private val broadCastService: BroadCastService,
                         private val transactionExecutor: TransactionExecutor) {

    init {
        broadCastService.addListener(broadCastService.REQUEST_TRANSACTION_CHANNEL) { requestTransactionEvent(Transaction.parseFrom(it)) }
    }

    companion object {
        val logger = logger<BlockProducerService>()
    }

    fun executeTransaction(transaction: Transaction) {
        transactionExecutor.execute(transaction)
    }

    fun requestTransaction(transaction: Transaction) {
        if (!transaction.verifySignature()) {
            logger.error("[INVALID SIGNATURE]")
            throw RuntimeException("invalid signature")
        }

        broadCastService.requestTransaction(transaction)
    }

    private fun requestTransactionEvent(transaction: Transaction) {
        if (!transaction.verifySignature()) {
            logger.error("[INVALID SIGNATURE]")
            throw RuntimeException("invalid signature")
        }

        databaseService.saveTransactionMemPool(transaction)
    }

    fun findTransactionFromMempool(count: Long): List<Transaction> {
        return databaseService.findTransactionMemPool(count)
    }

}
