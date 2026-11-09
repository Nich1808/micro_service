package kh.edu.istad.platform.iamapplicationservice.query.permission;

public record PermissionFilterQuery(
        int page,
        int size,
        String sort
) {
    public PermissionFilterQuery {
        if (page < 0 || size < 1) {
            throw new IllegalArgumentException("Invalid pagination values");
        }
    }
}
