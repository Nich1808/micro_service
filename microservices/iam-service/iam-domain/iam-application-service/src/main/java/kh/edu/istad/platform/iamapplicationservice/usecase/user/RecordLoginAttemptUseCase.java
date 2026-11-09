package kh.edu.istad.platform.iamapplicationservice.usecase.user;

public interface RecordLoginAttemptUseCase {
    void execute(String usernameOrEmail, boolean success);
}
