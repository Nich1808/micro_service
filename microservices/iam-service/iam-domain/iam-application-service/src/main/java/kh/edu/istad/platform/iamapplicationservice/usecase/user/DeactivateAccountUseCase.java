package kh.edu.istad.platform.iamapplicationservice.usecase.user;

import java.util.UUID;

/**
 * Self-service account deactivation requested by the account owner: the account is
 * disabled, every session is revoked and the request is recorded in the activity feed.
 */
public interface DeactivateAccountUseCase {

    void execute(UUID userUuid, String reason);
}
