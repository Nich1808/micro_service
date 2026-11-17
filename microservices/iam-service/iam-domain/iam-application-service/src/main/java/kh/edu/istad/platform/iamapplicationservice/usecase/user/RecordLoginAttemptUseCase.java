package kh.edu.istad.platform.iamapplicationservice.usecase.user;

import kh.edu.istad.platform.iamapplicationservice.command.security.LoginAttemptCommand;

public interface RecordLoginAttemptUseCase {

    /**
     * Updates the failure counter / last-login time and appends the attempt to the
     * user's login history.
     */
    void execute(LoginAttemptCommand command);
}
