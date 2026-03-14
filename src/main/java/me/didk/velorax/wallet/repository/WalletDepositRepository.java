package me.didk.velorax.wallet.repository;

import me.didk.velorax.wallet.domain.WalletDepositEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletDepositRepository extends JpaRepository<WalletDepositEntity, UUID> {
    Page<WalletDepositEntity> findAllByUserId(UUID userId, Pageable pageable);
}
