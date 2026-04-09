package me.didk.velorax.wallet.service;

import me.didk.common.exception.ConflictException;
import me.didk.common.exception.NotFoundException;
import me.didk.velorax.wallet.domain.TransferStatus;
import me.didk.velorax.wallet.domain.WalletDepositEntity;
import me.didk.velorax.wallet.dto.CreateDepositRequest;
import me.didk.velorax.wallet.repository.WalletDepositRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class DepositService {
    private static final String REF_TYPE_DEPOSIT = "DEPOSIT";

    private final WalletDepositRepository walletDepositRepository;
    private final WalletService walletService;

    public DepositService(
            WalletDepositRepository walletDepositRepository,
            WalletService walletService
    ) {
        this.walletDepositRepository = walletDepositRepository;
        this.walletService = walletService;
    }

    @Transactional(readOnly = true)
    public Page<WalletDepositEntity> list(UUID userId, Pageable pageable) {
        return walletDepositRepository.findAllByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public WalletDepositEntity getById(UUID userId, UUID depositId) {
        return walletDepositRepository.findById(depositId)
                .filter(deposit -> deposit.getUserId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Deposit not found"));
    }

    @Transactional
    public WalletDepositEntity create(UUID userId, CreateDepositRequest request) {
        String asset = normalizeRequired(request.asset(), "asset");
        String network = normalizeRequired(request.network(), "network");
        String address = normalizeRequired(request.address(), "address");
        BigDecimal amount = requiredPositive(request.amount(), "amount");

        WalletDepositEntity deposit = new WalletDepositEntity();
        deposit.setUserId(userId);
        deposit.setAsset(asset);
        deposit.setNetwork(network);
        deposit.setAddress(address);
        deposit.setTxHash(normalizeNullable(request.txHash()));
        deposit.setAmount(amount);
        deposit.setStatus(TransferStatus.PENDING);
        return walletDepositRepository.save(deposit);
    }

    @Transactional
    public WalletDepositEntity updateStatus(UUID userId, UUID depositId, TransferStatus newStatus, String txHash) {
        WalletDepositEntity deposit = getById(userId, depositId);
        TransferStatus currentStatus = deposit.getStatus();

        if (currentStatus == TransferStatus.CONFIRMED && newStatus != TransferStatus.CONFIRMED) {
            throw new ConflictException("Confirmed deposit status cannot be changed");
        }
        if (currentStatus != TransferStatus.CONFIRMED && newStatus == TransferStatus.CONFIRMED) {
            walletService.creditAvailable(
                    userId,
                    deposit.getAsset(),
                    deposit.getAmount(),
                    REF_TYPE_DEPOSIT,
                    deposit.getId()
            );
        }

        deposit.setStatus(newStatus);
        String normalizedTxHash = normalizeNullable(txHash);
        if (normalizedTxHash != null) {
            deposit.setTxHash(normalizedTxHash);
        }
        return walletDepositRepository.save(deposit);
    }

    private static String normalizeRequired(String value, String field) {
        String normalized = normalizeNullable(value);
        if (normalized == null) {
            throw new IllegalArgumentException(field + " is required");
        }
        return normalized;
    }

    private static String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private static BigDecimal requiredPositive(BigDecimal value, String field) {
        if (value == null || value.signum() <= 0) {
            throw new IllegalArgumentException(field + " must be greater than zero");
        }
        return value;
    }
}
