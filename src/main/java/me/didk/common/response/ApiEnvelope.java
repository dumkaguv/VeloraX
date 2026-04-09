package me.didk.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiEnvelope", description = "Standard successful API response")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiEnvelope<T>(
        @Schema(description = "Indicates whether the request was successful", example = "true")
        boolean success,
        @Schema(description = "Short response message", example = "Success")
        String message,
        @Schema(description = "Response payload")
        T data
) {
    public static <T> ApiEnvelope<T> success(String message, T data) {
        return new ApiEnvelope<>(true, message, data);
    }
}
