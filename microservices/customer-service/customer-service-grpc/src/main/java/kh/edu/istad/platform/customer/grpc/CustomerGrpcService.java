
package kh.edu.istad.platform.customer.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import kh.edu.istad.grpc.customer.CustomerResponse;
import kh.edu.istad.grpc.customer.CustomerServiceGrpc;
import kh.edu.istad.grpc.customer.GetCustomerRequest;
import kh.edu.istad.grpc.customer.GetCustomersRequest;
import kh.edu.istad.platform.customer.domain.usecase.QueryCustomerUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
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
        log.info("[gRPC SERVER] Received getCustomers request");

        queryCustomerUseCase.findAll()
                .map(customer -> CustomerResponse.newBuilder()
                        .setCustomerId(customer.getId().value().toString())
                        .setName(customer.getGivenName() + " " + customer.getFamilyName())
                        .setEmail(customer.getEmail().value())
                        .build())
                .subscribe(
                        response -> {
                            log.info("[gRPC SERVER] Sending customer: id={}",
                                    response.getCustomerId());

                            responseObserver.onNext(response);
                        },
                        error -> {
                            log.error("[gRPC SERVER] getCustomers failed: {}",
                                    error.getMessage(), error);

                            responseObserver.onError(error);
                        },
                        () -> {
                            log.info("[gRPC SERVER] getCustomers completed successfully");

                            responseObserver.onCompleted();
                        }
                );
    }

    @Override
    public void getCustomer(
            GetCustomerRequest request,
            StreamObserver<CustomerResponse> responseObserver
    ) {
        String customerId = request.getCustomerId();

        log.info("[gRPC SERVER] Received getCustomer request: customerId={}",
                customerId);

        queryCustomerUseCase.findById(customerId)
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

                            log.info("[gRPC SERVER] Customer found: id={}, name={}",
                                    response.getCustomerId(),
                                    response.getName());

                            responseObserver.onNext(response);
                            responseObserver.onCompleted();

                            log.info("[gRPC SERVER] getCustomer completed successfully");
                        },
                        error -> {
                            log.error("[gRPC SERVER] getCustomer failed: customerId={}, error={}",
                                    customerId, error.getMessage());

                            responseObserver.onError(
                                    error instanceof IllegalArgumentException
                                            ? Status.INVALID_ARGUMENT
                                            .withDescription("Invalid customer ID")
                                            .asRuntimeException()
                                            : error
                            );
                        }
                );
    }
}
