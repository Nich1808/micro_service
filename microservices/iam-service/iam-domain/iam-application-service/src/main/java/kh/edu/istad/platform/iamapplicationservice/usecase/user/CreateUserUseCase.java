package kh.edu.istad.platform.iamapplicationservice.usecase.user;

import kh.edu.istad.platform.iamapplicationservice.command.user.CreateUserCommand;
import kh.edu.istad.platform.iamapplicationservice.result.user.UserResult;

public interface CreateUserUseCase {
    UserResult execute(CreateUserCommand command);
}
