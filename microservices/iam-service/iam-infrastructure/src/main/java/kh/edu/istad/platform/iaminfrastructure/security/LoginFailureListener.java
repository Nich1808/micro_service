package kh.edu.istad.platform.iaminfrastructure.security;

import kh.edu.istad.platform.iamapplicationservice.usecase.user.RecordLoginAttemptUseCase;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;

/**
 * Records failed sign-ins, which is what makes brute-force detection possible.
 */
public class LoginFailureListener
        implements ApplicationListener<AbstractAuthenticationFailureEvent> {

    private final LoginHistoryRecorder recorder;

    public LoginFailureListener(RecordLoginAttemptUseCase recordLoginAttemptUseCase) {
        this.recorder = new LoginHistoryRecorder(recordLoginAttemptUseCase);
    }

    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
        recorder.record(
                event.getAuthentication(),
                false,
                event.getException().getClass().getSimpleName()
        );
    }
}
