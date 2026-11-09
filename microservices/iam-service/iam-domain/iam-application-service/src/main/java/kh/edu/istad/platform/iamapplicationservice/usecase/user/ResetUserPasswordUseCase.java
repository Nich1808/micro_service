package kh.edu.istad.platform.iamapplicationservice.usecase.user;

import kh.edu.istad.platform.iamapplicationservice.command.user.ResetUserPasswordCommand;

public interface ResetUserPasswordUseCase {
    void execute(ResetUserPasswordCommand command);
}
