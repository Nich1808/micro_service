package kh.edu.istad.platform.iamapplicationservice.usecase.user;

import kh.edu.istad.platform.iamapplicationservice.result.user.AuthUserResult;

public interface LoadUserForAuthenticationUseCase {
    AuthUserResult execute(String usernameOrEmail);
}
