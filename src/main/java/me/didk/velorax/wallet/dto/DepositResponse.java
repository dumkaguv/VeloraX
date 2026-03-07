package me.didk.velorax.wallet.dto;

import me.didk.velorax.wallet.domain.WalletDepositEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DepositResponse(
        UUID id,
        String asset,
        String network,
        String address,
        String txHash,
        BigDecimal amount,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public static DepositResponse from(WalletDepositEntity entity) {
        return new DepositResponse(
                entity.getId(),
                entity.getAsset(),
                entity.getNetwork(),
                entity.getAddress(),
                entity.getTxHash(),
                entity.getAmount(),
                entity.getStatus().name(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
