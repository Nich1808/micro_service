
package kh.edu.istad.platform.order.client.adapter;

import io.grpc.stub.StreamObserver;
import build.buf.gen.customer.CustomerResponse;
import build.buf.gen.customer.CustomerServiceGrpc;
import build.buf.gen.customer.GetCustomerRequest;
import build.buf.gen.customer.GetCustomersRequest;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class CustomerGrpcClient {

    private final CustomerServiceGrpc.CustomerServiceBlockingStub blockingStub;
    private final CustomerServiceGrpc.CustomerServiceStub asyncStub;

    public CustomerGrpcClient(
            CustomerServiceGrpc.CustomerServiceBlockingStub blockingStub,
            CustomerServiceGrpc.CustomerServiceStub asyncStub
    ) {
        this.blockingStub = blockingStub;
        this.asyncStub = asyncStub;
    }

    public CustomerResponse getCustomer(String customerId) {

        log.info("Calling Customer gRPC getCustomer, customerId={}", customerId);

        GetCustomerRequest request = GetCustomerRequest.newBuilder()
                .setCustomerId(customerId)
                .build();

        try {
            CustomerResponse response = blockingStub.getCustomer(request);

            log.info("gRPC getCustomer SUCCESS: {}", response);

            return response;

        } catch (Exception e) {
            log.error("gRPC getCustomer FAILED: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Flux<CustomerResponse> getCustomers() {

        log.info("Calling Customer gRPC getCustomers");

        return Flux.create(sink -> {
            GetCustomersRequest request = GetCustomersRequest.newBuilder()
                    .build();

            asyncStub.getCustomers(request, new StreamObserver<>() {

                @Override
                public void onNext(CustomerResponse response) {
                    log.info("gRPC received customer: {}", response);
                    sink.next(response);
                }

                @Override
                public void onError(Throwable error) {
                    log.error("gRPC getCustomers FAILED: {}", error.getMessage(), error);
                    sink.error(error);
                }

                @Override
                public void onCompleted() {
                    log.info("gRPC getCustomers COMPLETED successfully");
                    sink.complete();
                }
            });
        });
    }
}
