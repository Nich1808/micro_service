package kh.edu.istad.platform.iamapplicationservice.service.role;

import kh.edu.istad.platform.iamapplicationservice.port.out.RoleRepository;
import kh.edu.istad.platform.iamapplicationservice.result.role.RoleResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.GetRoleUseCase;
import kh.edu.istad.platform.iamdomaincore.model.Role;

import java.util.UUID;

public class GetRoleService implements GetRoleUseCase {

    private final RoleRepository roleRepository;

    public GetRoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleResult execute(UUID roleUuid) {
        if (roleUuid == null) {
            throw new IllegalArgumentException("Role UUID is required");
        }

        Role role = roleRepository.findByUuid(roleUuid)
                .filter(found -> !found.deleted())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        return new RoleResult(
                role.uuid(),
                role.name(),
                role.description(),
                role.system(),
                role.active(),
                role.createdAt(),
                role.updatedAt()
        );
    }
}
