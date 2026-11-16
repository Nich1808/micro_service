package kh.edu.istad.platform.iamapplicationservice.service.permission;

import kh.edu.istad.platform.iamapplicationservice.port.out.PermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.query.permission.PermissionFilterQuery;
import kh.edu.istad.platform.iamapplicationservice.result.common.PageResult;
import kh.edu.istad.platform.iamapplicationservice.result.permission.PermissionResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.ListPermissionsUseCase;

import java.util.List;

public class ListPermissionsService implements ListPermissionsUseCase {

    private final PermissionRepository permissionRepository;

    public ListPermissionsService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public PageResult<PermissionResult> execute(PermissionFilterQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("Permission filter query is required");
        }

        List<PermissionResult> content = permissionRepository
                .findAll(query.page(), query.size(), query.sort())
                .stream()
                .map(permission -> new PermissionResult(
                        permission.uuid(),
                        permission.name(),
                        permission.resource(),
                        permission.action(),
                        permission.description(),
                        permission.system(),
                        permission.active(),
                        permission.createdAt(),
                        permission.updatedAt()
                ))
                .toList();

        long totalElements = permissionRepository.count();

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
