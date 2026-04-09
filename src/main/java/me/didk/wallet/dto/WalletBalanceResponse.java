package me.didk.wallet.dto;

import me.didk.wallet.domain.WalletBalanceEntity;

import java.math.BigDecimal;

public record WalletBalanceResponse(
        String asset,
        BigDecimal available,
        BigDecimal locked
) {
    public static WalletBalanceResponse from(WalletBalanceEntity entity) {
        return new WalletBalanceResponse(
                entity.getAsset(),
                entity.getAvailable(),
                entity.getLocked()
        );
    }
}
