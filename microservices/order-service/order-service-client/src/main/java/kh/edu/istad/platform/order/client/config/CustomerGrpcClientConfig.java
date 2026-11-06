package kh.edu.istad.platform.order.client.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kh.edu.istad.grpc.customer.CustomerServiceGrpc;
import kh.edu.istad.platform.order.client.adapter.CustomerGrpcClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerGrpcClientConfig {

    @Bean(destroyMethod = "shutdown")
    public ManagedChannel customerManagedChannel() {
        return ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext()
                .build();
    }

    @Bean
    public CustomerServiceGrpc.CustomerServiceBlockingStub customerServiceBlockingStub(
            ManagedChannel customerManagedChannel
    ) {
        return CustomerServiceGrpc.newBlockingStub(customerManagedChannel);
    }

    @Bean
    public CustomerGrpcClient customerGrpcClient(
            CustomerServiceGrpc.CustomerServiceBlockingStub blockingStub,
            CustomerServiceGrpc.CustomerServiceStub asyncStub
    ) {
        return new CustomerGrpcClient(blockingStub, asyncStub);
    }

    @Bean
    public CustomerServiceGrpc.CustomerServiceStub customerServiceAsyncStub(
            ManagedChannel customerManagedChannel
    ) {
        return CustomerServiceGrpc.newStub(customerManagedChannel);
    }
}