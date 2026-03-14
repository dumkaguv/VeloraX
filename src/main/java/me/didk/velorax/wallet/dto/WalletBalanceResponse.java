package me.didk.velorax.wallet.dto;

import me.didk.velorax.wallet.domain.WalletBalanceEntity;

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
