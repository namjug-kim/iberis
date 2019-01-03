package com.iberis.node.client

import com.iberis.core.block.Block
import com.linecorp.armeria.client.ClientBuilder
import org.springframework.stereotype.Service

/**
 * Please describe the role of the BlockGrpcClientService
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-16
 *
 * @author namjug.kim
 * @since 2018-10-16
 * @version 0.1
 */
@Service
class BlockGrpcClientService {
    fun getBlock(height: Int): Block {
        val blockClientServiceGrpc = ClientBuilder("gproto+http://127.0.0.1:8080/")
                .defaultResponseTimeoutMillis(10000)
                .build(com.iberis.protocol.BlockServiceGrpc.BlockServiceBlockingStub::class.java)

        val getBlockRequest = com.iberis.protocol.Api.BlockHeight.newBuilder().setBlockHeight(height).build()
        val protocolBlock = blockClientServiceGrpc.getBlock(getBlockRequest)
        return Block.parseFrom(protocolBlock)
    }
}