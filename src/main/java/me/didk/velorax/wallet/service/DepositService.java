package me.didk.velorax.wallet.service;

import me.didk.velorax.wallet.domain.WalletDepositEntity;
import me.didk.velorax.wallet.repository.WalletDepositRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DepositService {
    private final WalletDepositRepository walletDepositRepository;

    public DepositService(WalletDepositRepository walletDepositRepository) {
        this.walletDepositRepository = walletDepositRepository;
    }

    @Transactional(readOnly = true)
    public Page<WalletDepositEntity> list(UUID userId, Pageable pageable) {
        return walletDepositRepository.findAllByUserId(userId, pageable);
    }
}
