package me.didk.user.dto;

import jakarta.validation.constraints.Size;
import me.didk.user.domain.UserRole;

public record UpdateUserRequest(
        @Size(max = 120)
        String displayName,

        UserRole role
) {
}
