
package kh.edu.istad.platform.order.client.adapter;

import io.grpc.stub.StreamObserver;
import kh.edu.istad.grpc.customer.CustomerResponse;
import kh.edu.istad.grpc.customer.CustomerServiceGrpc;
import kh.edu.istad.grpc.customer.GetCustomerRequest;
import kh.edu.istad.grpc.customer.GetCustomersRequest;
import reactor.core.publisher.Flux;

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
        GetCustomerRequest request = GetCustomerRequest.newBuilder()
                .setCustomerId(customerId)
                .build();

        return blockingStub.getCustomer(request);
    }

    public Flux<CustomerResponse> getCustomers() {
        return Flux.create(sink -> {
            GetCustomersRequest request = GetCustomersRequest.newBuilder()
                    .build();

            asyncStub.getCustomers(request, new StreamObserver<>() {

                @Override
                public void onNext(CustomerResponse response) {
                    sink.next(response);
                }

                @Override
                public void onError(Throwable error) {
                    sink.error(error);
                }

                @Override
                public void onCompleted() {
                    sink.complete();
                }
            });
        });
    }
}
