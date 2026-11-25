package kh.edu.istad.platform.iaminfrastructure.security;

import kh.edu.istad.platform.iamapplicationservice.usecase.user.RecordLoginAttemptUseCase;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;

/**
 * Records successful sign-ins.
 *
 * <p>Listens to the <em>interactive</em> success event on purpose: the generic
 * {@code AuthenticationSuccessEvent} is also published for every bearer-token request, which
 * would turn each API call into a fake entry in the login history.
 */
public class LoginSuccessListener
        implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    private final LoginHistoryRecorder recorder;

    public LoginSuccessListener(RecordLoginAttemptUseCase recordLoginAttemptUseCase) {
        this.recorder = new LoginHistoryRecorder(recordLoginAttemptUseCase);
    }

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        recorder.record(event.getAuthentication(), true, null);
    }
}
