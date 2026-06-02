package org.Job.command.service.impl;

import org.Job.command.command.CreateJobCategoryCommand;
import org.Job.command.command.UpdateJobCategoryCommand;
import org.Job.command.command.DeleteJobCategoryCommand;
import org.Job.command.data.JobCategoryRepository;
import org.Job.command.model.request.CreateJobCategoryRequest;
import org.Job.command.model.request.UpdateJobCategoryRequest;
import org.Job.command.service.JobCategoryService;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class JobCategoryServiceImpl implements JobCategoryService {

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private JobCategoryRepository jobCategoryRepository;

    @Override
    public CompletableFuture<String> createJobCategory(Jwt jwt, CreateJobCategoryRequest request) {
        if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không xác định được user từ token");
        }

        if (!hasAdminRole(jwt)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền quản lý danh mục");
        }

        String name = request.getName().trim();
        if (jobCategoryRepository.existsByName(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên danh mục đã tồn tại");
        }

        String slug = toSlug(name);

        CreateJobCategoryCommand command = CreateJobCategoryCommand.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .slug(slug)
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .active(true)
                .build();

        return commandGateway.send(command);
    }

    @Override
    public CompletableFuture<String> updateJobCategory(Jwt jwt, String categoryId, UpdateJobCategoryRequest request) {
        if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không xác định được user từ token");
        }

        if (!hasAdminRole(jwt)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền quản lý danh mục");
        }

        if (!jobCategoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục công việc");
        }

        String name = request.getName() != null ? request.getName().trim() : null;
        String slug = null;
        if (name != null) {
            if (name.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên danh mục không được để trống");
            }
            if (jobCategoryRepository.existsByNameAndIdNot(name, categoryId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên danh mục đã tồn tại");
            }
            slug = toSlug(name);
        }

        UpdateJobCategoryCommand command = UpdateJobCategoryCommand.builder()
                .id(categoryId)
                .name(name)
                .slug(slug)
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .active(request.getActive())
                .build();

        return commandGateway.send(command);
    }

    @Override
    public CompletableFuture<String> deleteJobCategory(Jwt jwt, String categoryId) {
        if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không xác định được user từ token");
        }

        if (!hasAdminRole(jwt)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền quản lý danh mục");
        }

        if (!jobCategoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục công việc");
        }

        DeleteJobCategoryCommand command = DeleteJobCategoryCommand.builder()
                .id(categoryId)
                .build();

        return commandGateway.send(command);
    }



    private boolean hasAdminRole(Jwt jwt) {
        if (hasRoleInRealmAccess(jwt, "SYSTEM_ADMIN", "ROLE_SYSTEM_ADMIN", "ADMIN", "ROLE_ADMIN")) {
            return true;
        }
        if (hasRoleInResourceAccess(jwt, "SYSTEM_ADMIN", "ROLE_SYSTEM_ADMIN", "ADMIN", "ROLE_ADMIN")) {
            return true;
        }
        if (containsRole(jwt.getClaim("authorities"), "SYSTEM_ADMIN", "ROLE_SYSTEM_ADMIN", "ADMIN", "ROLE_ADMIN")) {
            return true;
        }

        String scope = jwt.getClaimAsString("scope");
        if (scope != null && Arrays.stream(scope.split("\\s+"))
                .map(String::trim)
                .anyMatch(s -> "system_admin".equalsIgnoreCase(s) 
                        || "role_system_admin".equalsIgnoreCase(s) 
                        || "admin".equalsIgnoreCase(s) 
                        || "role_admin".equalsIgnoreCase(s))) {
            return true;
        }

        Object scpClaim = jwt.getClaim("scp");
        return containsRole(scpClaim, "system_admin", "role_system_admin", "admin", "role_admin");
    }

    private boolean hasRoleInRealmAccess(Jwt jwt, String... expectedRoles) {
        Object realmAccess = jwt.getClaim("realm_access");
        if (!(realmAccess instanceof Map<?, ?> realmMap)) {
            return false;
        }
        return containsRole(realmMap.get("roles"), expectedRoles);
    }

    private boolean hasRoleInResourceAccess(Jwt jwt, String... expectedRoles) {
        Object resourceAccess = jwt.getClaim("resource_access");
        if (!(resourceAccess instanceof Map<?, ?> resourceMap)) {
            return false;
        }

        String preferredClient = jwt.getClaimAsString("azp");
        if (preferredClient != null) {
            Object clientAccess = resourceMap.get(preferredClient);
            if (containsRole(clientAccess, expectedRoles)) {
                return true;
            }
        }

        return resourceMap.values().stream().anyMatch(value -> containsRole(value, expectedRoles));
    }

    private boolean containsRole(Object claimValue, String... expectedRoles) {
        Set<String> expected = Arrays.stream(expectedRoles)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        if (claimValue instanceof Collection<?> roles) {
            return roles.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(String::toLowerCase)
                    .anyMatch(expected::contains);
        }

        if (claimValue instanceof Map<?, ?> mapClaim) {
            Object directRoles = mapClaim.get("roles");
            if (containsRole(directRoles, expectedRoles)) {
                return true;
            }
            return mapClaim.values().stream().anyMatch(v -> containsRole(v, expectedRoles));
        }

        return false;
    }

    private static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}
