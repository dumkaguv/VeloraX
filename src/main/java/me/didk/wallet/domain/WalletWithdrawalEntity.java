package me.didk.wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "wallet_withdrawals")
public class WalletWithdrawalEntity {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 16)
    private String asset;

    @Column(nullable = false, length = 32)
    private String network;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, precision = 36, scale = 18)
    private BigDecimal amount;

    @Column(nullable = false, precision = 36, scale = 18)
    private BigDecimal fee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TransferStatus status;

    @Column(name = "client_withdrawal_id", length = 64)
    private String clientWithdrawalId;

    @Column(name = "idempotency_key", length = 128)
    private String idempotencyKey;

    @Column(name = "provider_ref", length = 128)
    private String providerRef;

    @Column(name = "error_message", length = 255)
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    public String getClientWithdrawalId() {
        return clientWithdrawalId;
    }

    public void setClientWithdrawalId(String clientWithdrawalId) {
        this.clientWithdrawalId = clientWithdrawalId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getProviderRef() {
        return providerRef;
    }

    public void setProviderRef(String providerRef) {
        this.providerRef = providerRef;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
