package kh.edu.istad.platform.iamapplicationservice.usecase.session;

import java.util.UUID;

public interface TerminateAllSessionsUseCase {

    /**
     * @param userUuid     owner of the sessions
     * @param keepKey      session key to keep alive (the caller's own device), may be null
     * @return number of sessions revoked
     */
    int execute(UUID userUuid, String keepKey);
}
