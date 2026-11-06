
package kh.edu.istad.platform.customer.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GrpcServerConfig {

    @Bean(destroyMethod = "shutdown")
    public Server grpcServer(
            CustomerGrpcService customerGrpcService
    ) throws java.io.IOException {

        Server server = ServerBuilder
                .forPort(9090)
                .addService(customerGrpcService)
                .addService(ProtoReflectionService.newInstance())
                .build();

        server.start();

        log.info("======================================");
        log.info("Customer gRPC Server started successfully");
        log.info("gRPC Server listening on port: {}",
                server.getPort());
        log.info("======================================");

        return server;
    }
}
