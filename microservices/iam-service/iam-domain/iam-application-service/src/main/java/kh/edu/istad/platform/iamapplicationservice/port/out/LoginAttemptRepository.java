package kh.edu.istad.platform.iamapplicationservice.port.out;

import kh.edu.istad.platform.iamdomaincore.model.LoginAttempt;

import java.time.OffsetDateTime;
import java.util.List;

public interface LoginAttemptRepository {

    LoginAttempt save(LoginAttempt attempt);

    List<LoginAttempt> findAllByUserId(Long userId, int page, int size);

    long countByUserId(Long userId);

    /**
     * Failed attempts since the given instant, newest first. Used to decide whether an
     * attempt belongs to a brute-force burst.
     */
    List<LoginAttempt> findFailuresSince(Long userId, OffsetDateTime since);
}
