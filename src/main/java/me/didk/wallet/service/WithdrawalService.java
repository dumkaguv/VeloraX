package me.didk.wallet.service;

import me.didk.common.exception.ConflictException;
import me.didk.common.exception.NotFoundException;
import me.didk.wallet.domain.TransferStatus;
import me.didk.wallet.domain.WalletWithdrawalEntity;
import me.didk.wallet.dto.CreateWithdrawalRequest;
import me.didk.wallet.repository.WalletWithdrawalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WithdrawalService {
    private static final String REF_TYPE_WITHDRAWAL = "WITHDRAWAL";
    private static final String REF_TYPE_WITHDRAWAL_CANCEL = "WITHDRAWAL_CANCEL";
    private static final String REF_TYPE_WITHDRAWAL_SETTLE = "WITHDRAWAL_SETTLE";
    private static final String REF_TYPE_WITHDRAWAL_FAILED = "WITHDRAWAL_FAILED";

    private final WalletWithdrawalRepository walletWithdrawalRepository;
    private final WalletService walletService;

    public WithdrawalService(
            WalletWithdrawalRepository walletWithdrawalRepository,
            WalletService walletService
    ) {
        this.walletWithdrawalRepository = walletWithdrawalRepository;
        this.walletService = walletService;
    }

    @Transactional
    public WalletWithdrawalEntity create(
            UUID userId,
            String idempotencyKey,
            CreateWithdrawalRequest request
    ) {
        String normalizedIdempotencyKey = normalizeNullable(idempotencyKey);
        String normalizedClientWithdrawalId = normalizeNullable(request.clientWithdrawalId());
        if (normalizedIdempotencyKey == null && normalizedClientWithdrawalId == null) {
            throw new IllegalArgumentException("Idempotency-Key header or clientWithdrawalId is required");
        }

        WalletWithdrawalEntity existing = findExisting(userId, normalizedIdempotencyKey, normalizedClientWithdrawalId);
        if (existing != null) {
            return existing;
        }

        String asset = normalizeRequired(request.asset(), "asset");
        String network = normalizeRequired(request.network(), "network");
        String address = normalizeRequired(request.address(), "address");
        BigDecimal amount = requiredPositive(request.amount(), "amount");
        BigDecimal fee = request.fee() == null ? BigDecimal.ZERO : nonNegative(request.fee(), "fee");

        WalletWithdrawalEntity withdrawal = new WalletWithdrawalEntity();
        withdrawal.setUserId(userId);
        withdrawal.setAsset(asset);
        withdrawal.setNetwork(network);
        withdrawal.setAddress(address);
        withdrawal.setAmount(amount);
        withdrawal.setFee(fee);
        withdrawal.setStatus(TransferStatus.PENDING);
        withdrawal.setClientWithdrawalId(normalizedClientWithdrawalId);
        withdrawal.setIdempotencyKey(normalizedIdempotencyKey);

        WalletWithdrawalEntity saved = walletWithdrawalRepository.save(withdrawal);
        walletService.lockFunds(
                userId,
                asset,
                amount.add(fee),
                REF_TYPE_WITHDRAWAL,
                saved.getId()
        );
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<WalletWithdrawalEntity> list(UUID userId, Pageable pageable) {
        return walletWithdrawalRepository.findAllByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public WalletWithdrawalEntity getById(UUID userId, UUID withdrawalId) {
        return walletWithdrawalRepository.findById(withdrawalId)
                .filter(withdrawal -> withdrawal.getUserId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Withdrawal not found"));
    }

    @Transactional
    public WalletWithdrawalEntity cancel(UUID userId, UUID withdrawalId) {
        WalletWithdrawalEntity withdrawal = getById(userId, withdrawalId);
        if (withdrawal.getStatus() != TransferStatus.PENDING) {
            throw new ConflictException("Withdrawal cannot be canceled in current status");
        }

        walletService.releaseFunds(
                userId,
                withdrawal.getAsset(),
                withdrawal.getAmount().add(withdrawal.getFee()),
                REF_TYPE_WITHDRAWAL_CANCEL,
                withdrawal.getId()
        );
        withdrawal.setStatus(TransferStatus.CANCELED);
        return walletWithdrawalRepository.save(withdrawal);
    }

    @Transactional
    public WalletWithdrawalEntity updateStatus(UUID userId, UUID withdrawalId, TransferStatus newStatus, String errorMessage) {
        WalletWithdrawalEntity withdrawal = getById(userId, withdrawalId);
        TransferStatus currentStatus = withdrawal.getStatus();
        if (currentStatus == newStatus) {
            return withdrawal;
        }
        if (currentStatus != TransferStatus.PENDING) {
            throw new ConflictException("Only pending withdrawals can be updated");
        }

        BigDecimal lockAmount = withdrawal.getAmount().add(withdrawal.getFee());
        if (newStatus == TransferStatus.CONFIRMED) {
            walletService.settleLocked(
                    userId,
                    withdrawal.getAsset(),
                    lockAmount,
                    REF_TYPE_WITHDRAWAL_SETTLE,
                    withdrawal.getId()
            );
        } else if (newStatus == TransferStatus.FAILED) {
            walletService.releaseFunds(
                    userId,
                    withdrawal.getAsset(),
                    lockAmount,
                    REF_TYPE_WITHDRAWAL_FAILED,
                    withdrawal.getId()
            );
        } else if (newStatus == TransferStatus.CANCELED) {
            walletService.releaseFunds(
                    userId,
                    withdrawal.getAsset(),
                    lockAmount,
                    REF_TYPE_WITHDRAWAL_CANCEL,
                    withdrawal.getId()
            );
        }

        withdrawal.setStatus(newStatus);
        String normalizedError = normalizeNullable(errorMessage);
        if (normalizedError != null) {
            withdrawal.setErrorMessage(normalizedError);
        }
        return walletWithdrawalRepository.save(withdrawal);
    }

    private WalletWithdrawalEntity findExisting(
            UUID userId,
            String idempotencyKey,
            String clientWithdrawalId
    ) {
        if (idempotencyKey != null) {
            return walletWithdrawalRepository.findByUserIdAndIdempotencyKey(userId, idempotencyKey).orElse(null);
        }
        if (clientWithdrawalId != null) {
            return walletWithdrawalRepository.findByUserIdAndClientWithdrawalId(userId, clientWithdrawalId).orElse(null);
        }
        return null;
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

    private static BigDecimal nonNegative(BigDecimal value, String field) {
        if (value.signum() < 0) {
            throw new IllegalArgumentException(field + " must not be negative");
        }
        return value;
    }
}
