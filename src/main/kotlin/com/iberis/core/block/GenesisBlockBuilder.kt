package com.iberis.core.block

import com.iberis.account.Account
import com.iberis.common.Coin
import com.iberis.core.contract.AddBlockProducerContract
import com.iberis.core.contract.BlockProduceRewardContract
import com.iberis.core.transaction.Transaction
import com.iberis.core.transaction.TransactionBuilder
import com.iberis.crypto.Hash
import java.time.Instant

/**
 * Please describe the role of the GenesisBlock
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-12
 *
 * @author namjug.kim
 * @since 2018-10-12
 * @version 0.1
 */
class GenesisBlockBuilder(val account: Account) {
    private fun createBlockHeader(transactions: List<Transaction>): BlockHeader {
        val version: Long = 1
        val prevBlockHash = Hash.ByteWrapper("initial_parent_hash".toByteArray()).build()
        val time = Instant.now().toEpochMilli()
        return BlockHeader(version, prevBlockHash, time, BlockHeader.buildMerkleTree(transactions))
    }

    private fun createGenesisTransaction(account: Account): List<Transaction> {
        return listOf(
                TransactionBuilder(account.privateKey!!)
                        .sender(account.publicKey)
                        .contract(BlockProduceRewardContract(account.publicKey, Coin(100000000000)))
                        .nonce(0)
                        .build(),
                TransactionBuilder(account.privateKey)
                        .sender(account.publicKey)
                        .contract(AddBlockProducerContract(account.publicKey))
                        .nonce(1)
                        .build()
        )
    }

    fun build(): Block {
        val transactions = createGenesisTransaction(account)
        val blockHeader = createBlockHeader(transactions)
        val blockHash = blockHeader.calculateBlockHash()

        return Block(
                blockHash = blockHash,
                blockHeader = blockHeader,
                transactions = transactions,
                blockProducerAddress = account.publicKey,
                blockProducerSignature = Block.generateSignature(account.privateKey!!, account.publicKey, blockHash)
        )
    }
}
