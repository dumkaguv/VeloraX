package me.didk.velorax.wallet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.didk.velorax.wallet.domain.TransferStatus;

public record UpdateTransferStatusRequest(
        @NotNull
        TransferStatus status,

        @Size(max = 128)
        String txHash,

        @Size(max = 255)
        String errorMessage
) {
}
