package kh.edu.istad.platform.iamapplicationservice.query.role;

public record RoleFilterQuery(
        int page,
        int size,
        String sort
) {
    public RoleFilterQuery {
        if (page < 0 || size < 1) {
            throw new IllegalArgumentException("Invalid pagination values");
        }
    }
}
