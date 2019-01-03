package com.iberis.db

import com.iberis.account.Account
import com.iberis.consensus.model.BlockProducer
import com.iberis.consensus.model.CommitBlock
import com.iberis.core.block.Block
import com.iberis.core.transaction.Transaction
import com.iberis.crypto.Hash
import com.iberis.util.createAddress

/**
 * Please describe the role of the DatabaseService
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-11
 *
 * @author namjug.kim
 * @since 2018-10-11
 * @version 0.1
 */
class DatabaseService {
    private val blockDbHandler = TestDbHandler(
            deserializeFunction = { byteArray -> Block.parseFrom(byteArray) },
            serializeFunction = { value -> value.rawData() }
    )

    private val transactionDbHandler = TestDbHandler(
            deserializeFunction = { byteArray -> Transaction.parseFrom(byteArray) },
            serializeFunction = { value -> value.rawData() }
    )

    private val accountDbHandler = TestDbHandler(
            deserializeFunction = { byteArray -> Account.parseFrom(byteArray) },
            serializeFunction = { value -> value.rawData() }
    )

    private val transactionMemPoolHandler = TestDbHandler(
            deserializeFunction = { byteArray -> Transaction.parseFrom(byteArray) },
            serializeFunction = { value -> value.rawData() }
    )

    private val blockProducerDbHandler = TestDbHandler(
            deserializeFunction = { byteArray -> BlockProducer.parseFrom(byteArray) },
            serializeFunction = { value -> value.rawData() }
    )

    private val preCommitDbHandler = TestDbHandler(
            deserializeFunction = { byteArray -> CommitBlock.parseFrom(byteArray) },
            serializeFunction = { value -> value.rawData() }
    )

    private val commitDbHandler = TestDbHandler(
            deserializeFunction = { byteArray -> CommitBlock.parseFrom(byteArray) },
            serializeFunction = { value -> value.rawData() }
    )

    fun saveBlock(block: Block) {
        blockDbHandler.putIbAbsent(block.blockHash.hex, block)
    }

    fun findBlock(index: Int): Block? {
        return blockDbHandler.getIndex(index)
    }

    fun findFirstBlock(): Block? {
        return blockDbHandler.getFirst()
    }

    fun findLastBlock(): Block? {
        return blockDbHandler.getLast()
    }

    fun findAllBlock(): List<Block> {
        return blockDbHandler.getAll()
    }

    fun saveAccount(account: Account) {
        accountDbHandler.put(account.accountId.address, account)
    }

    fun findAccount(address: String): Account? {
        return accountDbHandler.get(address)
    }

    fun findAllAccount(): List<Account> {
        return accountDbHandler.getAll()
    }

    fun saveTransaction(transaction: Transaction) {
        transactionDbHandler.put(transaction.transactionId.hex, transaction)
    }

    fun findTransaction(transactionId: String): Transaction? {
        return transactionDbHandler.get(transactionId)
    }

    fun saveTransactionMemPool(transaction: Transaction) {
        transactionMemPoolHandler.put(transaction.transactionId.hex, transaction)
    }

    fun findTransactionMemPool(limit: Long): List<Transaction> {
        return transactionMemPoolHandler.get(limit)
    }

    fun removeTransactionMemPool(transaction: Transaction) {
        transactionMemPoolHandler.remove(transaction.transactionId.hex)
    }

    fun addBlockProducer(blockProducer: BlockProducer) {
        blockProducerDbHandler.put(blockProducer.accountId.address, blockProducer)
    }

    fun findBlockProducer(blockProducerAddress: String): BlockProducer? {
        return blockProducerDbHandler.get(blockProducerAddress)
    }

    fun findBlockProducers(): List<BlockProducer> {
        return blockProducerDbHandler.getAll()
    }

    fun addPreCommitBlock(commitBlock: CommitBlock) {
        val key = commitBlock.block.blockHash.hex + "-" + commitBlock.witness.createAddress()
        preCommitDbHandler.put(key, commitBlock)
    }

    fun findAllPreCommitBlock(blockHash: Hash): List<CommitBlock> {
        // todo db 구조 변경
        return preCommitDbHandler.getAll()
                .filter { it.block.blockHash == blockHash }
    }

    fun addCommitBlock(commitBlock: CommitBlock) {
        val key = commitBlock.block.blockHash.hex + "-" + commitBlock.witness.createAddress()
        commitDbHandler.put(key, commitBlock)
    }

    fun findAllCommitBlock(blockHash: Hash): List<CommitBlock> {
        // todo db 구조 변경
        return commitDbHandler.getAll()
                .filter { it.block.blockHash == blockHash }
    }
}
