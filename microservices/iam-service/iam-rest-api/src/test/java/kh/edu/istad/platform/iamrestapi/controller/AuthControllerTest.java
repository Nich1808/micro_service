package kh.edu.istad.platform.iamrestapi.controller;

import kh.edu.istad.platform.iamapplicationservice.command.user.RegisterUserCommand;
import kh.edu.istad.platform.iamapplicationservice.result.user.UserResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.RegisterUserUseCase;
import kh.edu.istad.platform.iamrestapi.error.ApiExceptionHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-layer contract for registration: request validation, ProblemDetail errors and the
 * response payload defined in the assignment.
 */
class AuthControllerTest {

    private static final String VALID_BODY = """
            {
              "username": "sokdara",
              "email": "dara.sok@istad.co",
              "password": "Str0ng!Pass2026",
              "confirmPassword": "Str0ng!Pass2026",
              "givenName": "Dara",
              "familyName": "Sok",
              "locale": "km"
            }
            """;

    private RegisterUserUseCase registerUserUseCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        registerUserUseCase = mock(RegisterUserUseCase.class);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(registerUserUseCase))
                .setControllerAdvice(new ApiExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void registerReturnsCreatedUser() throws Exception {
        when(registerUserUseCase.execute(any(RegisterUserCommand.class)))
                .thenReturn(new UserResult(
                        UUID.fromString("7f3d2a10-91c4-4b0e-8d6f-2a9c5e1b7d40"),
                        "sokdara",
                        "dara.sok@istad.co",
                        false,
                        "Dara Sok",
                        "km",
                        true,
                        List.of(),
                        OffsetDateTime.parse("2026-11-05T02:15:00Z"),
                        OffsetDateTime.parse("2026-11-05T02:15:00Z")
                ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid")
                        .value("7f3d2a10-91c4-4b0e-8d6f-2a9c5e1b7d40"))
                .andExpect(jsonPath("$.username").value("sokdara"))
                .andExpect(jsonPath("$.email").value("dara.sok@istad.co"))
                .andExpect(jsonPath("$.name").value("Dara Sok"))
                .andExpect(jsonPath("$.locale").value("km"))
                .andExpect(jsonPath("$.enabled").value(true));

        ArgumentCaptor<RegisterUserCommand> captor =
                ArgumentCaptor.forClass(RegisterUserCommand.class);
        verify(registerUserUseCase).execute(captor.capture());

        assertEquals("sokdara", captor.getValue().username());
        assertEquals("km", captor.getValue().locale());
    }

    @Test
    void invalidPayloadIsReportedAsProblemDetail() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "",
                                  "email": "not-an-email",
                                  "password": "short",
                                  "confirmPassword": "x",
                                  "givenName": "",
                                  "familyName": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type")
                        .value("https://iam.istad.co/problems/validation-error"))
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray());

        verify(registerUserUseCase, never()).execute(any());
    }

    @Test
    void mismatchedPasswordsAreRejected() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY.replace(
                                "\"confirmPassword\": \"Str0ng!Pass2026\"",
                                "\"confirmPassword\": \"Different!Pass2026\""
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Passwords do not match"));

        verify(registerUserUseCase, never()).execute(any());
    }
}
