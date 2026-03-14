package me.didk.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import me.didk.user.domain.UserRole;

public record CreateUserRequest(
        @NotBlank
        @Email
        @Size(max = 254)
        String email,

        @NotBlank
        @Size(max = 120)
        String displayName,

        UserRole role
) {
}
