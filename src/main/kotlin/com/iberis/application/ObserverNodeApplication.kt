package com.iberis.application

import com.iberis.common.logger
import com.iberis.consensus.BlockProducerService
import com.iberis.core.block.BlockService
import com.iberis.core.contract.AddBlockProducerContract
import com.iberis.core.transaction.TransactionBuilder
import com.iberis.core.transaction.TransactionService
import com.iberis.network.client.BlockGrpcClientService
import io.reactivex.Flowable
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import java.util.stream.Stream

/**
 * Please describe the role of the ObserverNodeApplication
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-16
 *
 * @author namjug.kim
 * @since 2018-10-16
 * @version 0.1
 */
@Profile("observerNode")
@Service
class ObserverNodeApplication(private val blockProducerService: BlockProducerService,
                              private val blockGrpcClientService: BlockGrpcClientService,
                              private val transactionService: TransactionService,
                              private val blockService: BlockService) : ApplicationStarter {

    companion object {
        val logger = logger<ObserverNodeApplication>()
    }

    override fun run() {
        try {
            Stream.iterate(0) { i -> i + 1 }
                    .forEach {
                        val block = blockGrpcClientService.getBlock(it)
                        logger.info("sync block with height : {}", it)
                        if (it == 0) {
                            blockService.executeGenesisBlock(block)
                        } else {
                            blockService.executeBlock(block)
                        }
                    }
        } catch (e: Exception) {
            logger.error(e.message)
        }

        val myAccount = ApplicationProperties.myAccount
        transactionService.requestTransaction(TransactionBuilder(myAccount.privateKey!!)
                .sender(myAccount.publicKey)
                .contract(AddBlockProducerContract(myAccount.publicKey))
                .nonce(myAccount.nonce)
                .build())

        Flowable.interval(ApplicationProperties.blockProduceInterval / 3, TimeUnit.MILLISECONDS)
                .doOnNext {
                    try {
                        blockProducerService.produceBlock()
                    } catch (e: Exception) {
                        logger.error(e.message, e)
                    }
                }
                .onBackpressureDrop()
                .subscribe()
    }
}
