package me.didk.velorax.wallet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.didk.velorax.wallet.domain.TransferStatus;

public record UpdateTransferStatusRequest(
        @NotNull
        @Schema(implementation = TransferStatus.class)
        TransferStatus status,

        @Size(max = 128)
        String txHash,

        @Size(max = 255)
        String errorMessage
) {
}
