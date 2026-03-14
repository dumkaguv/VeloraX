package me.didk.user.dto;

import me.didk.user.domain.UserEntity;
import me.didk.user.domain.UserRole;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String displayName,
        UserRole role,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserResponse from(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
