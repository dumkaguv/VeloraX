package me.didk.velorax.wallet.dto;

import me.didk.velorax.wallet.domain.WalletWithdrawalEntity;

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
        String status,
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
                entity.getStatus().name(),
                entity.getClientWithdrawalId(),
                entity.getIdempotencyKey(),
                entity.getProviderRef(),
                entity.getErrorMessage(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
