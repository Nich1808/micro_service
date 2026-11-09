package kh.edu.istad.platform.iamapplicationservice.result.common;

import java.util.List;

public record PageResult<T>(
        List<T> content,
        Page page
) {
    public PageResult {
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
