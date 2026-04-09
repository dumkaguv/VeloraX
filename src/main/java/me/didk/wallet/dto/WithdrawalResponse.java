package me.didk.wallet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import me.didk.wallet.domain.TransferStatus;
import me.didk.wallet.domain.WalletWithdrawalEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WithdrawalResponse(
        UUID id,
        String asset,
        String network,
        String address,
        BigDecimal amount,
        BigDecimal fee,
        @Schema(implementation = TransferStatus.class)
        TransferStatus status,
        String clientWithdrawalId,
        String idempotencyKey,
        String providerRef,
        String errorMessage,
        Instant createdAt,
        Instant updatedAt
) {
    public static WithdrawalResponse from(WalletWithdrawalEntity entity) {
        return new WithdrawalResponse(
                entity.getId(),
                entity.getAsset(),
                entity.getNetwork(),
                entity.getAddress(),
                entity.getAmount(),
                entity.getFee(),
                entity.getStatus(),
                entity.getClientWithdrawalId(),
                entity.getIdempotencyKey(),
                entity.getProviderRef(),
                entity.getErrorMessage(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
