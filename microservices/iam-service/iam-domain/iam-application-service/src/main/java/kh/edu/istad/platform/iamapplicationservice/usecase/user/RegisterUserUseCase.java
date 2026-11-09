package kh.edu.istad.platform.iamapplicationservice.usecase.user;

import kh.edu.istad.platform.iamapplicationservice.command.user.RegisterUserCommand;
import kh.edu.istad.platform.iamapplicationservice.result.user.UserResult;

public interface RegisterUserUseCase {
    UserResult execute(RegisterUserCommand command);
}
