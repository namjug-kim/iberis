package com.iberis.core.block

import com.iberis.account.Account
import com.iberis.common.Coin
import com.iberis.core.contract.TransferContract
import com.iberis.core.transaction.TransactionBuilder
import com.iberis.db.DatabaseService
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit4.SpringRunner
import java.time.Instant

/**
 * Please describe the role of the BlockTest
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-10
 *
 * @author namjug.kim
 * @version 0.1
 * @since 2018-10-10
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@Import(TestConfiguration::class)
class BlockTest {

    @Configuration
    open class TestConfiguration {
        @Bean
        open fun databaseService(): DatabaseService {
            return DatabaseService()
        }
    }

    @Test
    fun `create block`() {
        val genesisBlockWallet = Account.createAccount()
        val genesisBlock = GenesisBlockBuilder(genesisBlockWallet).build()

        val walletA = Account.createAccount()
        val transaction = TransactionBuilder(genesisBlockWallet.privateKey!!)
                .nonce(0)
                .sender(genesisBlockWallet.publicKey)
                .contract(TransferContract(genesisBlockWallet.publicKey, walletA.publicKey, Coin(10)))
                .build()
        val transactions = listOf(transaction)
        val blockHeader = BlockHeader(1, genesisBlock.blockHash, Instant.now().toEpochMilli(), BlockHeader.buildMerkleTree(transactions))
        val transferContractBlock = Block(
                blockHeader = blockHeader,
                blockHash = blockHeader.calculateBlockHash(),
                transactions = transactions,
                blockProducerAddress = walletA.publicKey,
                blockProducerSignature = Block.generateSignature(walletA.privateKey!!, walletA.publicKey, blockHeader.calculateBlockHash())
        )

        assertEquals(transferContractBlock.blockHeader.prevBlockHash, genesisBlock.blockHash)
    }

}
