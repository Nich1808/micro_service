package kh.edu.istad.platform.iamrestapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iamapplicationservice.query.permission.PermissionFilterQuery;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.ActivatePermissionUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.CreatePermissionUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.DeactivatePermissionUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.DeletePermissionUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.GetPermissionUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.ListPermissionsUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.UpdatePermissionUseCase;
import kh.edu.istad.platform.iamrestapi.dto.request.CreatePermissionRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.UpdatePermissionRequest;
import kh.edu.istad.platform.iamrestapi.dto.response.PageResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.PermissionResponse;
import kh.edu.istad.platform.iamrestapi.mapper.ApiMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final CreatePermissionUseCase createPermissionUseCase;
    private final ListPermissionsUseCase listPermissionsUseCase;
    private final GetPermissionUseCase getPermissionUseCase;
    private final UpdatePermissionUseCase updatePermissionUseCase;
    private final DeletePermissionUseCase deletePermissionUseCase;
    private final ActivatePermissionUseCase activatePermissionUseCase;
    private final DeactivatePermissionUseCase deactivatePermissionUseCase;

    public PermissionController(
            CreatePermissionUseCase createPermissionUseCase,
            ListPermissionsUseCase listPermissionsUseCase,
            GetPermissionUseCase getPermissionUseCase,
            UpdatePermissionUseCase updatePermissionUseCase,
            DeletePermissionUseCase deletePermissionUseCase,
            ActivatePermissionUseCase activatePermissionUseCase,
            DeactivatePermissionUseCase deactivatePermissionUseCase
    ) {
        this.createPermissionUseCase = createPermissionUseCase;
        this.listPermissionsUseCase = listPermissionsUseCase;
        this.getPermissionUseCase = getPermissionUseCase;
        this.updatePermissionUseCase = updatePermissionUseCase;
        this.deletePermissionUseCase = deletePermissionUseCase;
        this.activatePermissionUseCase = activatePermissionUseCase;
        this.deactivatePermissionUseCase = deactivatePermissionUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('permission:create')")
    public PermissionResponse create(
            @Valid @RequestBody CreatePermissionRequest request
    ) {
        return ApiMapper.toResponse(
                createPermissionUseCase.execute(ApiMapper.toCommand(request))
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('permission:read')")
    public PageResponse<PermissionResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort
    ) {
        return ApiMapper.toPageResponse(
                listPermissionsUseCase.execute(
                        new PermissionFilterQuery(page, size, sort)
                ),
                ApiMapper::toResponse
        );
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('permission:read')")
    public PermissionResponse get(@PathVariable UUID uuid) {
        return ApiMapper.toResponse(getPermissionUseCase.execute(uuid));
    }

    @PatchMapping("/{uuid}")
    @PreAuthorize("hasAuthority('permission:update')")
    public PermissionResponse update(
            @PathVariable UUID uuid,
            @Valid @RequestBody UpdatePermissionRequest request
    ) {
        return ApiMapper.toResponse(
                updatePermissionUseCase.execute(ApiMapper.toCommand(uuid, request))
        );
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('permission:delete')")
    public void delete(@PathVariable UUID uuid) {
        deletePermissionUseCase.execute(uuid);
    }

    @PostMapping("/{uuid}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('permission:update')")
    public void activate(@PathVariable UUID uuid) {
        activatePermissionUseCase.execute(uuid);
    }

    @PostMapping("/{uuid}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('permission:update')")
    public void deactivate(@PathVariable UUID uuid) {
        deactivatePermissionUseCase.execute(uuid);
    }
}
