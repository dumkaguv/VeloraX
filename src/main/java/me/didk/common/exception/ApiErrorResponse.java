package me.didk.common.exception;

public record ApiErrorResponse(
        boolean success,
        String error,
        String message
) {
}
