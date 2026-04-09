package me.didk.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(name = "ApiErrorResponse", description = "Standard error API response")
public record ApiErrorResponse(
        @Schema(description = "Indicates whether the request was successful", example = "false")
        boolean success,
        @Schema(description = "HTTP status code", example = "404")
        int status,
        @Schema(description = "HTTP status reason", example = "Not Found")
        String error,
        @Schema(description = "Detailed error message", example = "User not found")
        String message,
        @Schema(description = "Request path", example = "/api/v1/users/123e4567-e89b-12d3-a456-426614174000")
        String path,
        @Schema(description = "Timestamp when the error occurred", example = "2026-04-09T10:15:30Z")
        Instant timestamp
) {
}
