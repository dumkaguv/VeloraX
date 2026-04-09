package me.didk.wallet.dto;

import me.didk.wallet.domain.WalletAddressEntity;

import java.time.Instant;

public record DepositAddressResponse(
        String asset,
        String network,
        String address,
        Instant createdAt
) {
    public static DepositAddressResponse from(WalletAddressEntity entity) {
        return new DepositAddressResponse(
                entity.getAsset(),
                entity.getNetwork(),
                entity.getAddress(),
                entity.getCreatedAt()
        );
    }
}
