package kh.edu.istad.platform.iampersistence.adapter;

import org.springframework.data.domain.Sort;

import java.util.Set;

final class SortSupport {

    private SortSupport() {
    }

    static Sort userSort(String sort) {
        return resolve(
                sort,
                Set.of("createdAt", "updatedAt", "username", "email", "enabled"),
                "createdAt"
        );
    }

    static Sort roleSort(String sort) {
        return resolve(
                sort,
                Set.of("createdAt", "updatedAt", "name", "active"),
                "createdAt"
        );
    }

    static Sort permissionSort(String sort) {
        return resolve(
                sort,
                Set.of("createdAt", "updatedAt", "name", "resource", "action", "active"),
                "createdAt"
        );
    }

    private static Sort resolve(
            String sort,
            Set<String> allowedFields,
            String defaultField
    ) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, defaultField);
        }

        String[] parts = sort.split(",", 2);
        String field = parts[0].trim();

        if (!allowedFields.contains(field)) {
            field = defaultField;
        }

        Sort.Direction direction = parts.length > 1
                && "asc".equalsIgnoreCase(parts[1].trim())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Sort.by(direction, field);
    }
}
