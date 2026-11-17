package kh.edu.istad.platform.iamapplicationservice.usecase.session;

import kh.edu.istad.platform.iamapplicationservice.result.session.UserSessionResult;

import java.util.List;
import java.util.UUID;

public interface ListUserSessionsUseCase {

    /**
     * @param userUuid     owner of the sessions
     * @param currentKey   session key of the caller, used to flag the current device
     * @return active sessions, most recently active first
     */
    List<UserSessionResult> execute(UUID userUuid, String currentKey);
}
