package kh.edu.istad.platform.iaminfrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import kh.edu.istad.platform.iamapplicationservice.command.security.LoginAttemptCommand;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.RecordLoginAttemptUseCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Shared logic for the two authentication-event listeners: resolves the caller's device
 * details from the current request and appends the attempt to the login history.
 */
final class LoginHistoryRecorder {

    private static final Log LOG = LogFactory.getLog(LoginHistoryRecorder.class);

    private final RecordLoginAttemptUseCase recordLoginAttemptUseCase;

    LoginHistoryRecorder(RecordLoginAttemptUseCase recordLoginAttemptUseCase) {
        this.recordLoginAttemptUseCase = recordLoginAttemptUseCase;
    }

    void record(
            Authentication authentication,
            boolean successful,
            String failureReason
    ) {
        if (authentication == null || authentication.getName() == null) {
            return;
        }

        String identifier = authentication.getName().trim();

        if (identifier.isEmpty() || "anonymousUser".equals(identifier)) {
            return;
        }

        try {
            ClientDeviceInfo device = currentDevice();

            recordLoginAttemptUseCase.execute(new LoginAttemptCommand(
                    identifier,
                    successful,
                    failureReason,
                    device == null ? null : device.ipAddress(),
                    device == null ? null : device.device(),
                    device == null ? null : device.browser(),
                    device == null ? null : device.operatingSystem(),
                    device == null ? null : device.location(),
                    device == null ? null : device.userAgent()
            ));
        } catch (RuntimeException ex) {
            // Login history must never break authentication.
            LOG.warn("Unable to record login attempt for " + identifier, ex);
        }
    }

    private ClientDeviceInfo currentDevice() {
        if (RequestContextHolder.getRequestAttributes()
                instanceof ServletRequestAttributes attributes) {
            HttpServletRequest request = attributes.getRequest();
            return ClientDeviceInfo.from(request);
        }

        return null;
    }
}
