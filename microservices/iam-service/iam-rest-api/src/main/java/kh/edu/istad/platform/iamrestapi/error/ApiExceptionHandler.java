package kh.edu.istad.platform.iamrestapi.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Renders every API failure as an RFC 9457 {@link ProblemDetail}.
 */
@RestControllerAdvice(basePackages = "kh.edu.istad.platform.iamrestapi")
public class ApiExceptionHandler {

    private static final String BASE_TYPE = "https://iam.istad.co/problems/";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::describe)
                .toList();

        ProblemDetail problem = problem(
                HttpStatus.BAD_REQUEST,
                "validation-error",
                "Validation failed"
        );
        problem.setProperty("errors", errors);

        return problem;
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail handleNotFound(NoSuchElementException ex) {
        return problem(
                HttpStatus.NOT_FOUND,
                "not-found",
                message(ex, "Resource not found")
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleBadRequest(IllegalArgumentException ex) {
        HttpStatus status = isNotFound(ex.getMessage())
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;

        return problem(
                status,
                status == HttpStatus.NOT_FOUND ? "not-found" : "invalid-request",
                message(ex, "Request is invalid")
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleConflict(IllegalStateException ex) {
        return problem(
                HttpStatus.CONFLICT,
                "conflict",
                message(ex, "Request conflicts with the current state")
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        return problem(
                HttpStatus.FORBIDDEN,
                "access-denied",
                "You are not allowed to perform this operation"
        );
    }

    private ProblemDetail problem(
            HttpStatus status,
            String slug,
            String title
    ) {
        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setType(URI.create(BASE_TYPE + slug));
        problem.setTitle(title);
        return problem;
    }

    private Map<String, String> describe(FieldError error) {
        return Map.of(
                "field", error.getField(),
                "message", error.getDefaultMessage() == null
                        ? "is invalid"
                        : error.getDefaultMessage()
        );
    }

    private String message(RuntimeException ex, String fallback) {
        String message = ex.getMessage();
        return message == null || message.isBlank() ? fallback : message;
    }

    private boolean isNotFound(String message) {
        return message != null
                && message.toLowerCase().contains("not found");
    }
}
