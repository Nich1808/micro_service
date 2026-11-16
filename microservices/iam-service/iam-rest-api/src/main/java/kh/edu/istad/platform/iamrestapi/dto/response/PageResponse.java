package kh.edu.istad.platform.iamrestapi.dto.response;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        Page page
) {
    public PageResponse {
        content = List.copyOf(content);
    }

    public record Page(
            int number,
            int size,
            long totalElements,
            int totalPages
    ) {
    }
}
