package me.didk.wallet.repository;

import me.didk.wallet.domain.WalletBalanceEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletBalanceRepository extends JpaRepository<WalletBalanceEntity, UUID> {
    List<WalletBalanceEntity> findAllByUserIdOrderByAssetAsc(UUID userId);

    Optional<WalletBalanceEntity> findByUserIdAndAsset(UUID userId, String asset);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from WalletBalanceEntity b where b.userId = :userId and b.asset = :asset")
    Optional<WalletBalanceEntity> findForUpdate(@Param("userId") UUID userId, @Param("asset") String asset);
}
