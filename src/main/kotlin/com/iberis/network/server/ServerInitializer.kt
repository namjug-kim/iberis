package com.iberis.network.server

import com.iberis.core.block.BlockService
import com.linecorp.armeria.common.SessionProtocol
import com.linecorp.armeria.server.ServerBuilder
import com.linecorp.armeria.server.grpc.GrpcServiceBuilder
import org.springframework.stereotype.Service

/**
 * Please describe the role of the ServerInitializer
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-16
 *
 * @author namjug.kim
 * @since 2018-10-16
 * @version 0.1
 */
@Service
class ServerInitializer(private val blockService: BlockService) {
    fun runServer() {
        val sb = ServerBuilder()
        sb.service(GrpcServiceBuilder().addService(BlockGrpcServerService(blockService)).build())
                .port(8080, SessionProtocol.HTTP)
                .build()
                .start()
    }
}
