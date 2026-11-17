package kh.edu.istad.platform.iaminfrastructure.security;

import kh.edu.istad.platform.iamapplicationservice.usecase.user.RecordLoginAttemptUseCase;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

/**
 * Records successful sign-ins.
 */
public class LoginSuccessListener
        implements ApplicationListener<AuthenticationSuccessEvent> {

    private final LoginHistoryRecorder recorder;

    public LoginSuccessListener(RecordLoginAttemptUseCase recordLoginAttemptUseCase) {
        this.recorder = new LoginHistoryRecorder(recordLoginAttemptUseCase);
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        recorder.record(event.getAuthentication(), true, null);
    }
}
