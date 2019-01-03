package com.iberis.node.server

import com.iberis.core.block.BlockService
import io.grpc.stub.StreamObserver

/**
 * Please describe the role of the BlockGrpcServerService
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-16
 *
 * @author namjug.kim
 * @since 2018-10-16
 * @version 0.1
 */
class BlockGrpcServerService(val blockService: BlockService) : com.iberis.protocol.BlockServiceGrpc.BlockServiceImplBase() {

    override fun getBlock(request: com.iberis.protocol.Api.BlockHeight?, responseObserver: StreamObserver<com.iberis.protocol.Protocol.PBlock>?) {
        request ?: throw IllegalArgumentException()
        responseObserver ?: throw IllegalArgumentException()

        responseObserver.onNext(blockService.findBlock(request.blockHeight).toProtobuf())
        responseObserver.onCompleted()
    }
}
