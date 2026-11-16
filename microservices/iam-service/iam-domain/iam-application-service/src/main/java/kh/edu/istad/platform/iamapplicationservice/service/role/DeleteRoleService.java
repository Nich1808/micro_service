package kh.edu.istad.platform.iamapplicationservice.service.role;

import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.RoleRepository;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.DeleteRoleUseCase;
import kh.edu.istad.platform.iamdomaincore.model.Role;

import java.time.OffsetDateTime;
import java.util.UUID;

public class DeleteRoleService implements DeleteRoleUseCase {

    private final RoleRepository roleRepository;
    private final CurrentActorProvider actorProvider;

    public DeleteRoleService(
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
                .filter(found -> !found.deleted())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        roleRepository.save(role.softDelete(actor, OffsetDateTime.now()));
    }
}
