package me.didk.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.domain.Page;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaginatedApiEnvelope<T>(
        boolean success,
        String message,
        T data,
        long total,
        int page,
        int totalPages,
        int pageSize,
        Integer nextPage,
        Integer prevPage
) {
    public static <T> PaginatedApiEnvelope<List<T>> success(String message, Page<T> page) {
        return success(
                message,
                page.getContent(),
                page.getTotalElements(),
                page.getNumber() + 1,
                page.getSize()
        );
    }

    public static <T> PaginatedApiEnvelope<T> success(String message, T data, long total, int page, int pageSize) {
        int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / pageSize);
        Integer nextPage = page < totalPages ? page + 1 : null;
        Integer prevPage = page > 1 ? page - 1 : null;
        return new PaginatedApiEnvelope<>(true, message, data, total, page, totalPages, pageSize, nextPage, prevPage);
    }
}
