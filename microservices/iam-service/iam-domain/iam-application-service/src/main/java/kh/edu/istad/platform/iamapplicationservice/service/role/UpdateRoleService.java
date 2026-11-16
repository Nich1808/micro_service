package kh.edu.istad.platform.iamapplicationservice.service.role;

import kh.edu.istad.platform.iamapplicationservice.command.role.UpdateRoleCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.RoleRepository;
import kh.edu.istad.platform.iamapplicationservice.result.role.RoleResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.UpdateRoleUseCase;
import kh.edu.istad.platform.iamdomaincore.model.Role;

import java.time.OffsetDateTime;
import java.util.Locale;

public class UpdateRoleService implements UpdateRoleUseCase {

    private final RoleRepository roleRepository;
    private final CurrentActorProvider actorProvider;

    public UpdateRoleService(
            RoleRepository roleRepository,
            CurrentActorProvider actorProvider
    ) {
        this.roleRepository = roleRepository;
        this.actorProvider = actorProvider;
    }

    @Override
    public RoleResult execute(UpdateRoleCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Update role command is required");
        }

        if (command.roleUuid() == null) {
            throw new IllegalArgumentException("Role UUID is required");
        }

        if (command.name() == null || command.name().isBlank()) {
            throw new IllegalArgumentException("Role name is required");
        }

        Role role = roleRepository.findByUuid(command.roleUuid())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        role.validateModifiable();

        String name = command.name().trim().toUpperCase(Locale.ROOT);

        if (!name.equals(role.name()) && roleRepository.existsByName(name)) {
            throw new IllegalArgumentException("Role already exists");
        }

        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        Role updated = roleRepository.save(role.withDetails(
                name,
                command.description(),
                actor,
                OffsetDateTime.now()
        ));

        return new RoleResult(
                updated.uuid(),
                updated.name(),
                updated.description(),
                updated.system(),
                updated.active(),
                updated.createdAt(),
                updated.updatedAt()
        );
    }
}
