package me.didk.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(name = "PaginatedApiEnvelope", description = "Standard paginated API response")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaginatedApiEnvelope<T>(
        @Schema(description = "Indicates whether the request was successful", example = "true")
        boolean success,
        @Schema(description = "Short response message", example = "Success")
        String message,
        @Schema(description = "Response payload")
        T data,
        @Schema(description = "Total number of records", example = "42")
        long total,
        @Schema(description = "Current page number", example = "1")
        int page,
        @Schema(description = "Total number of pages", example = "5")
        int totalPages,
        @Schema(description = "Requested page size", example = "10")
        int pageSize,
        @Schema(description = "Next page number if it exists", example = "2", nullable = true)
        Integer nextPage,
        @Schema(description = "Previous page number if it exists", example = "1", nullable = true)
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
