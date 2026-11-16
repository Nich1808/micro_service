package kh.edu.istad.platform.iamapplicationservice.service.role;

import kh.edu.istad.platform.iamapplicationservice.port.out.RoleRepository;
import kh.edu.istad.platform.iamapplicationservice.query.role.RoleFilterQuery;
import kh.edu.istad.platform.iamapplicationservice.result.common.PageResult;
import kh.edu.istad.platform.iamapplicationservice.result.role.RoleSummary;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.ListRolesUseCase;
import kh.edu.istad.platform.iamdomaincore.model.Role;

import java.util.List;

public class ListRolesService implements ListRolesUseCase {

    private final RoleRepository roleRepository;

    public ListRolesService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public PageResult<RoleSummary> execute(RoleFilterQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("Role filter query is required");
        }

        List<RoleSummary> content = roleRepository
                .findAll(query.page(), query.size(), query.sort())
                .stream()
                .map(role -> new RoleSummary(
                        role.uuid(),
                        role.name(),
                        role.active()
                ))
                .toList();

        long totalElements = roleRepository.count();

        return new PageResult<>(
                content,
                new PageResult.Page(
                        query.page(),
                        query.size(),
                        totalElements,
                        totalPages(totalElements, query.size())
                )
        );
    }

    private int totalPages(long totalElements, int size) {
        return (int) ((totalElements + size - 1) / size);
    }
}
