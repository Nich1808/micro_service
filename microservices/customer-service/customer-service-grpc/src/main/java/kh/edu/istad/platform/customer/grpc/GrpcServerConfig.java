package kh.edu.istad.platform.customer.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.grpc.protobuf.services.ProtoReflectionService;
import java.io.IOException;

@Configuration
public class GrpcServerConfig {

    @Bean(destroyMethod = "shutdown")
    public Server grpcServer(
            CustomerGrpcService customerGrpcService
    ) throws IOException {

        Server server = ServerBuilder
                .forPort(9090)
                .addService(customerGrpcService)
                .addService(ProtoReflectionService.newInstance())
                .build();

        server.start();

        return server;
    }
}