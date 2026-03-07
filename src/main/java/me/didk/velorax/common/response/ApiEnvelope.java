package me.didk.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiEnvelope<T>(
        boolean success,
        String message,
        T data,
        Long total,
        Integer page,
        Integer totalPages,
        Integer pageSize,
        Integer nextPage,
        Integer prevPage
) {
    public static <T> ApiEnvelope<T> success(String message, T data) {
        return new ApiEnvelope<>(true, message, data, null, null, null, null, null, null);
    }

    public static <T> ApiEnvelope<T> paginated(String message, T data, long total, int page, int pageSize) {
        int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / pageSize);
        Integer nextPage = page < totalPages ? page + 1 : null;
        Integer prevPage = page > 1 ? page - 1 : null;
        return new ApiEnvelope<>(
                true,
                message,
                data,
                total,
                page,
                totalPages,
                pageSize,
                nextPage,
                prevPage
        );
    }
}
