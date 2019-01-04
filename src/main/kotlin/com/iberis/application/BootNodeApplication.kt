package com.iberis.application

import com.iberis.common.logger
import com.iberis.consensus.BlockProducerService
import com.iberis.core.block.BlockService
import com.iberis.core.block.GenesisBlockBuilder
import com.iberis.network.server.ServerInitializer
import io.reactivex.Flowable
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

/**
 * Please describe the role of the BootNodeApplication
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-16
 *
 * @author namjug.kim
 * @since 2018-10-16
 * @version 0.1
 */
@Profile("bootNode")
@Service
class BootNodeApplication(private val blockService: BlockService,
                          private val blockProducerService: BlockProducerService) : ApplicationStarter {

    companion object {
        val logger = logger<BootNodeApplication>()
    }

    private val serverInitializer: ServerInitializer = ServerInitializer(blockService)

    override fun run() {
        serverInitializer.runServer()

        val myAccount = ApplicationProperties.myAccount
        val genesisBlock = GenesisBlockBuilder(myAccount).build()

        blockService.executeGenesisBlock(genesisBlock)


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
