package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.result.user.UserResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.GetUserUseCase;
import kh.edu.istad.platform.iamdomaincore.model.User;

import java.util.List;
import java.util.UUID;

public class GetUserService implements GetUserUseCase {

    private final UserRepository userRepository;

    public GetUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResult execute(UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .filter(u -> !u.deleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new UserResult(
                user.uuid(),
                user.username(),
                user.email(),
                user.emailVerified(),
                user.name(),
                user.locale(),
                user.enabled(),
                List.of(),
                user.createdAt(),
                user.updatedAt()
        );
    }
}
