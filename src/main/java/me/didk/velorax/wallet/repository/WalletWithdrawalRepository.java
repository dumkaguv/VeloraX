package me.didk.velorax.wallet.repository;

import me.didk.velorax.wallet.domain.WalletWithdrawalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletWithdrawalRepository extends JpaRepository<WalletWithdrawalEntity, UUID> {
    Page<WalletWithdrawalEntity> findAllByUserId(UUID userId, Pageable pageable);

    Optional<WalletWithdrawalEntity> findByUserIdAndClientWithdrawalId(UUID userId, String clientWithdrawalId);

    Optional<WalletWithdrawalEntity> findByUserIdAndIdempotencyKey(UUID userId, String idempotencyKey);
}
