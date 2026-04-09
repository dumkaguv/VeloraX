package me.didk.wallet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import me.didk.wallet.domain.TransferStatus;
import me.didk.wallet.domain.WalletDepositEntity;

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
        @Schema(implementation = TransferStatus.class)
        TransferStatus status,
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
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
