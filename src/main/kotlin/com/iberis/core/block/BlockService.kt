package com.iberis.core.block

import com.iberis.common.logger
import com.iberis.core.transaction.TransactionService
import com.iberis.db.DatabaseService
import com.iberis.util.createAddress
import org.springframework.stereotype.Service

/**
 * Please describe the role of the BlockService
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-16
 *
 * @author namjug.kim
 * @since 2018-10-16
 * @version 0.1
 */
@Service
class BlockService(val databaseService: DatabaseService,
                   val transactionService: TransactionService) {
    companion object {
        val logger = logger<BlockService>()
    }

    fun executeGenesisBlock(block: Block) {
        if (!block.verifySignature()) {
            logger.error("[INVALID BLOCK SIGNATURE] invalid block signature")
            throw RuntimeException("[INVALID BLOCK SIGNATURE] invalid block signature")
        }

        databaseService.saveBlock(block)
        block.transactions.forEach { transactionService.executeTransaction(it) }
    }

    fun currentHeight(): Int {
        return databaseService.findAllBlock().size
    }

    @Synchronized
    fun executeBlock(block: Block) {
        if (!block.verifySignature()) {
            logger.error("[INVALID BLOCK SIGNATURE] invalid block signature")
            throw RuntimeException("[INVALID BLOCK SIGNATURE] invalid block signature")
        }

        val lastBlock = databaseService.findLastBlock() ?: throw RuntimeException("last block is null")
        if (lastBlock.blockHash != block.blockHeader.prevBlockHash) {
            logger.error("[INVALID BLOCK CHAIN] invalid block chain. lastBlockHash : {}, newBlockPrevHash : {}", lastBlock.blockHash.hex, block.blockHeader.prevBlockHash.hex)
            throw RuntimeException("[INVALID BLOCK SIGNATURE] invalid block chain")
        }

        databaseService.saveBlock(block)
        block.transactions.forEach { transactionService.executeTransaction(it) }

        val sb = StringBuilder()
        sb.append("\n=========== ADD BLOCK =============\n")
                .append("\t [block producer] : ").append(block.blockProducerAddress.createAddress()).append("\n")
                .append("\t [block hash] : ").append(block.blockHash.hex).append("\n")
                .append("\t [block prev hash] : ").append(block.blockHeader.prevBlockHash.hex).append("\n")
                .append("\t [transactions]\n")

        block.transactions.forEach {
            sb.append("\t\t [sender] : ").append(it.sender.createAddress()).append("\n")
            sb.append("\t\t [contract] : ").append(it.contractType).append("\n")
        }
        sb.append("====================================\n")
        logger.info(sb.toString())
    }

    fun findBlock(height: Int): Block {
        return databaseService.findBlock(height) ?: throw RuntimeException("block not exists")
    }
}
