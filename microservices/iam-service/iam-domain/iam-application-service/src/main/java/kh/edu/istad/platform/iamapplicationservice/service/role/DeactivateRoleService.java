package kh.edu.istad.platform.iamapplicationservice.service.role;

import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.RoleRepository;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.DeactivateRoleUseCase;
import kh.edu.istad.platform.iamdomaincore.model.Role;

import java.time.OffsetDateTime;
import java.util.UUID;

public class DeactivateRoleService implements DeactivateRoleUseCase {

    private final RoleRepository roleRepository;
    private final CurrentActorProvider actorProvider;

    public DeactivateRoleService(
            RoleRepository roleRepository,
            CurrentActorProvider actorProvider
    ) {
        this.roleRepository = roleRepository;
        this.actorProvider = actorProvider;
    }

    @Override
    public void execute(UUID roleUuid) {
        if (roleUuid == null) {
            throw new IllegalArgumentException("Role UUID is required");
        }

        Role role = roleRepository.findByUuid(roleUuid)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        roleRepository.save(role.deactivate(actor, OffsetDateTime.now()));
    }
}
