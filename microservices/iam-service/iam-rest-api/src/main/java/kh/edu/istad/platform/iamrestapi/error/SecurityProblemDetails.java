package kh.edu.istad.platform.iamrestapi.error;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

/**
 * Writes RFC 9457 responses for failures raised by the security filters, which run
 * before the MVC exception handlers. Values are static, so no JSON escaping is
 * required.
 */
public final class SecurityProblemDetails {

    private static final String BASE_TYPE = "https://iam.istad.co/problems/";

    private SecurityProblemDetails() {
    }

    public static void unauthorized(HttpServletResponse response)
            throws IOException {
        write(response, HttpStatus.UNAUTHORIZED, "unauthorized",
                "Authentication is required");
    }

    public static void forbidden(HttpServletResponse response)
            throws IOException {
        write(response, HttpStatus.FORBIDDEN, "access-denied",
                "You are not allowed to perform this operation");
    }

    private static void write(
            HttpServletResponse response,
            HttpStatus status,
            String slug,
            String title
    ) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.getWriter().write(
                "{"
                        + "\"type\":\"" + BASE_TYPE + slug + "\","
                        + "\"title\":\"" + title + "\","
                        + "\"status\":" + status.value()
                        + "}"
        );
    }
}
