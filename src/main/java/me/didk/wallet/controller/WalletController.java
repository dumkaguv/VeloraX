package me.didk.wallet.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import me.didk.common.response.ApiEnvelope;
import me.didk.wallet.dto.DepositAddressResponse;
import me.didk.wallet.dto.WalletBalanceResponse;
import me.didk.wallet.service.WalletAddressService;
import me.didk.wallet.service.WalletService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@Tag(name = "Wallet")
@RequestMapping("/api/v1/wallet")
public class WalletController {
    private final WalletService walletService;
    private final WalletAddressService walletAddressService;

    public WalletController(
            WalletService walletService,
            WalletAddressService walletAddressService
    ) {
        this.walletService = walletService;
        this.walletAddressService = walletAddressService;
    }

    @GetMapping("/balances")
    public ApiEnvelope<List<WalletBalanceResponse>> balances(
            @RequestHeader("X-User-Id") UUID userId
    ) {
        List<WalletBalanceResponse> balances = walletService.getBalances(userId)
                .stream()
                .map(WalletBalanceResponse::from)
                .toList();
        return ApiEnvelope.success("Success", balances);
    }

    @GetMapping("/addresses")
    public ApiEnvelope<DepositAddressResponse> getOrCreateAddress(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam @NotBlank String asset
    ) {
        return ApiEnvelope.success(
                "Success",
                DepositAddressResponse.from(walletAddressService.getOrCreateDepositAddress(userId, asset))
        );
    }
}
