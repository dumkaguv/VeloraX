package me.didk.velorax.wallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateWithdrawalRequest(
        @NotBlank
        @Size(max = 16)
        String asset,

        @NotBlank
        @Size(max = 32)
        String network,

        @NotBlank
        @Size(max = 255)
        String address,

        @DecimalMin(value = "0.000000000000000001", inclusive = true)
        BigDecimal amount,

        @DecimalMin(value = "0", inclusive = true)
        BigDecimal fee,

        @Size(max = 64)
        String clientWithdrawalId
) {
}
