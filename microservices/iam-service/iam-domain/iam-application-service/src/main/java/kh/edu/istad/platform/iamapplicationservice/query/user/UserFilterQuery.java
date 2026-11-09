package kh.edu.istad.platform.iamapplicationservice.query.user;

public record UserFilterQuery(
        int page,
        int size,
        String sort
) {
    public UserFilterQuery {
        if (page < 0 || size < 1) {
            throw new IllegalArgumentException("Invalid pagination values");
        }
    }
}
