package me.didk.wallet.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Transfer status")
public enum TransferStatus {
    PENDING,
    CONFIRMED,
    FAILED,
    CANCELED
}
