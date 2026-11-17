package kh.edu.istad.platform.iamrestapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iamapplicationservice.command.admin.ClientCommand;
import kh.edu.istad.platform.iamapplicationservice.result.admin.ClientResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.admin.AdminUseCases;
import kh.edu.istad.platform.iamrestapi.dto.admin.AdminDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * OAuth2/OIDC client administration: credentials, grant types, redirect URIs and scopes.
 */
@RestController
@RequestMapping("/api/admin/clients")
public class AdminClientController {

    private final AdminUseCases.ListClients listClients;
    private final AdminUseCases.GetClient getClient;
    private final AdminUseCases.CreateClient createClient;
    private final AdminUseCases.UpdateClient updateClient;
    private final AdminUseCases.DeleteClient deleteClient;

    public AdminClientController(
            AdminUseCases.ListClients listClients,
            AdminUseCases.GetClient getClient,
            AdminUseCases.CreateClient createClient,
            AdminUseCases.UpdateClient updateClient,
            AdminUseCases.DeleteClient deleteClient
    ) {
        this.listClients = listClients;
        this.getClient = getClient;
        this.createClient = createClient;
        this.updateClient = updateClient;
        this.deleteClient = deleteClient;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('permission:read')")
    public List<AdminDto.ClientResponse> list() {
        return listClients.execute().stream().map(AdminClientController::toResponse).toList();
    }

    @GetMapping("/{clientId}")
    @PreAuthorize("hasAuthority('permission:read')")
    public AdminDto.ClientResponse get(@PathVariable String clientId) {
        return toResponse(getClient.execute(clientId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('permission:create')")
    public AdminDto.ClientResponse create(
            @Valid @RequestBody AdminDto.ClientRequest request
    ) {
        return toResponse(createClient.execute(toCommand(request)));
    }

    @PutMapping("/{clientId}")
    @PreAuthorize("hasAuthority('permission:update')")
    public AdminDto.ClientResponse update(
            @PathVariable String clientId,
            @Valid @RequestBody AdminDto.ClientRequest request
    ) {
        return toResponse(updateClient.execute(clientId, toCommand(request)));
    }

    @DeleteMapping("/{clientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('permission:delete')")
    public void delete(@PathVariable String clientId) {
        deleteClient.execute(clientId);
    }

    private static ClientCommand toCommand(AdminDto.ClientRequest request) {
        return new ClientCommand(
                request.clientId(),
                request.clientName(),
                request.clientSecret(),
                request.authenticationMethods(),
                request.grantTypes(),
                request.redirectUris(),
                request.postLogoutRedirectUris(),
                request.scopes(),
                Boolean.TRUE.equals(request.requireProofKey()),
                Boolean.TRUE.equals(request.requireConsent()),
                request.accessTokenTtlSeconds(),
                request.refreshTokenTtlSeconds()
        );
    }

    private static AdminDto.ClientResponse toResponse(ClientResult result) {
        return new AdminDto.ClientResponse(
                result.clientId(),
                result.clientName(),
                result.authenticationMethods(),
                result.grantTypes(),
                result.redirectUris(),
                result.postLogoutRedirectUris(),
                result.scopes(),
                result.requireProofKey(),
                result.requireConsent(),
                result.accessTokenTtlSeconds(),
                result.refreshTokenTtlSeconds(),
                result.confidential()
        );
    }
}
