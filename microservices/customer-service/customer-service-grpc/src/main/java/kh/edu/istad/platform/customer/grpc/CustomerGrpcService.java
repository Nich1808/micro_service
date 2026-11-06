
package kh.edu.istad.platform.customer.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import kh.edu.istad.grpc.customer.CustomerResponse;
import kh.edu.istad.grpc.customer.CustomerServiceGrpc;
import kh.edu.istad.grpc.customer.GetCustomerRequest;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.usecase.QueryCustomerUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import kh.edu.istad.grpc.customer.GetCustomersRequest;

@Component
@RequiredArgsConstructor
public class CustomerGrpcService
        extends CustomerServiceGrpc.CustomerServiceImplBase {

    private final QueryCustomerUseCase queryCustomerUseCase;


    @Override
    public void getCustomers(
            GetCustomersRequest request,
            StreamObserver<CustomerResponse> responseObserver
    ) {
        queryCustomerUseCase.findAll()
                .map(customer -> CustomerResponse.newBuilder()
                        .setCustomerId(customer.getId().value().toString())
                        .setName(customer.getGivenName() + " " + customer.getFamilyName())
                        .setEmail(customer.getEmail().value())
                        .build())
                .subscribe(
                        responseObserver::onNext,
                        responseObserver::onError,
                        responseObserver::onCompleted
                );
    }


    @Override
    public void getCustomer(
            GetCustomerRequest request,
            StreamObserver<CustomerResponse> responseObserver
    ) {

        queryCustomerUseCase.findById(request.getCustomerId())
                .switchIfEmpty(Mono.error(
                        Status.NOT_FOUND
                                .withDescription("Customer not found")
                                .asRuntimeException()
                ))
                .subscribe(
                        customer -> {
                            CustomerResponse response = CustomerResponse.newBuilder()
                                    .setCustomerId(customer.getId().value().toString())
                                    .setName(customer.getGivenName() + " " + customer.getFamilyName())
                                    .setEmail(customer.getEmail().value())
                                    .build();

                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        error -> responseObserver.onError(
                                error instanceof IllegalArgumentException
                                        ? Status.INVALID_ARGUMENT
                                        .withDescription("Invalid customer ID")
                                        .asRuntimeException()
                                        : error
                        )
                );
    }
}
