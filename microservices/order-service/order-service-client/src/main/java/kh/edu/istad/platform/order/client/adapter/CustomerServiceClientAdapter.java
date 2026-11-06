
package kh.edu.istad.platform.order.client.adapter;

import kh.edu.istad.platform.order.domain.dto.QueryCustomerResult;
import kh.edu.istad.platform.order.domain.port.out.client.CustomerServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class CustomerServiceClientAdapter implements CustomerServiceClient {

    private final CustomerGrpcClient customerGrpcClient;

    @Override
    public Flux<QueryCustomerResult> getCustomerNames() {
        return customerGrpcClient.getCustomers()
                .map(response -> {
                    String[] names = response.getName().split(" ", 2);

                    String givenName = names[0];
                    String familyName = names.length > 1 ? names[1] : "";

                    return new QueryCustomerResult(familyName, givenName);
                });
    }
}
