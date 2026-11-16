package kh.edu.istad.platform.iamrestapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iamapplicationservice.query.role.RoleFilterQuery;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.ActivateRoleUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.AssignPermissionsToRoleUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.CreateRoleUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.DeactivateRoleUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.DeleteRoleUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.GetRoleUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.ListRolesUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.RevokePermissionFromRoleUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.UpdateRoleUseCase;
import kh.edu.istad.platform.iamrestapi.dto.request.AssignPermissionsToRoleRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.CreateRoleRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.UpdateRoleRequest;
import kh.edu.istad.platform.iamrestapi.dto.response.PageResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.PermissionAssignmentResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.RoleResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.RoleSummaryResponse;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final CreateRoleUseCase createRoleUseCase;
    private final ListRolesUseCase listRolesUseCase;
    private final GetRoleUseCase getRoleUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;
    private final DeleteRoleUseCase deleteRoleUseCase;
    private final ActivateRoleUseCase activateRoleUseCase;
    private final DeactivateRoleUseCase deactivateRoleUseCase;
    private final AssignPermissionsToRoleUseCase assignPermissionsToRoleUseCase;
    private final RevokePermissionFromRoleUseCase revokePermissionFromRoleUseCase;

    public RoleController(
            CreateRoleUseCase createRoleUseCase,
            ListRolesUseCase listRolesUseCase,
            GetRoleUseCase getRoleUseCase,
            UpdateRoleUseCase updateRoleUseCase,
            DeleteRoleUseCase deleteRoleUseCase,
            ActivateRoleUseCase activateRoleUseCase,
            DeactivateRoleUseCase deactivateRoleUseCase,
            AssignPermissionsToRoleUseCase assignPermissionsToRoleUseCase,
            RevokePermissionFromRoleUseCase revokePermissionFromRoleUseCase
    ) {
        this.createRoleUseCase = createRoleUseCase;
        this.listRolesUseCase = listRolesUseCase;
        this.getRoleUseCase = getRoleUseCase;
        this.updateRoleUseCase = updateRoleUseCase;
        this.deleteRoleUseCase = deleteRoleUseCase;
        this.activateRoleUseCase = activateRoleUseCase;
        this.deactivateRoleUseCase = deactivateRoleUseCase;
        this.assignPermissionsToRoleUseCase = assignPermissionsToRoleUseCase;
        this.revokePermissionFromRoleUseCase = revokePermissionFromRoleUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('role:create')")
    public RoleResponse create(@Valid @RequestBody CreateRoleRequest request) {
        return ApiMapper.toResponse(
                createRoleUseCase.execute(ApiMapper.toCommand(request))
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('role:read')")
    public PageResponse<RoleSummaryResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort
    ) {
        return ApiMapper.toPageResponse(
                listRolesUseCase.execute(new RoleFilterQuery(page, size, sort)),
                ApiMapper::toResponse
        );
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('role:read')")
    public RoleResponse get(@PathVariable UUID uuid) {
        return ApiMapper.toResponse(getRoleUseCase.execute(uuid));
    }

    @PatchMapping("/{uuid}")
    @PreAuthorize("hasAuthority('role:update')")
    public RoleResponse update(
            @PathVariable UUID uuid,
            @Valid @RequestBody UpdateRoleRequest request
    ) {
        return ApiMapper.toResponse(
                updateRoleUseCase.execute(ApiMapper.toCommand(uuid, request))
        );
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('role:delete')")
    public void delete(@PathVariable UUID uuid) {
        deleteRoleUseCase.execute(uuid);
    }

    @PostMapping("/{uuid}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('role:update')")
    public void activate(@PathVariable UUID uuid) {
        activateRoleUseCase.execute(uuid);
    }

    @PostMapping("/{uuid}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('role:update')")
    public void deactivate(@PathVariable UUID uuid) {
        deactivateRoleUseCase.execute(uuid);
    }

    @PostMapping("/{uuid}/permissions")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('role:assign')")
    public List<PermissionAssignmentResponse> assignPermissions(
            @PathVariable UUID uuid,
            @Valid @RequestBody AssignPermissionsToRoleRequest request
    ) {
        return assignPermissionsToRoleUseCase
                .execute(ApiMapper.toCommand(uuid, request))
                .stream()
                .map(ApiMapper::toResponse)
                .toList();
    }

    @DeleteMapping("/{uuid}/permissions/{permissionUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('role:assign')")
    public void revokePermission(
            @PathVariable UUID uuid,
            @PathVariable UUID permissionUuid,
            @RequestParam(required = false) String reason
    ) {
        revokePermissionFromRoleUseCase.execute(uuid, permissionUuid, reason);
    }
}
