package kh.edu.istad.platform.iamapplicationservice.usecase.session;

import java.util.UUID;

public interface TerminateSessionUseCase {

    void execute(UUID userUuid, UUID sessionUuid, String reason);
}
