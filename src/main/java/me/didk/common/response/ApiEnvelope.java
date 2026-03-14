package me.didk.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiEnvelope<T>(
        boolean success,
        String message,
        T data
) {
    public static <T> ApiEnvelope<T> success(String message, T data) {
        return new ApiEnvelope<>(true, message, data);
    }
}
