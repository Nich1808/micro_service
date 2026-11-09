package kh.edu.istad.platform.iamapplicationservice.usecase.user;

import kh.edu.istad.platform.iamapplicationservice.command.user.UpdateUserProfileCommand;
import kh.edu.istad.platform.iamapplicationservice.result.user.UserResult;

public interface UpdateUserProfileUseCase {
    UserResult execute(UpdateUserProfileCommand command);
}
