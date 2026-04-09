package me.didk.wallet.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.didk.common.openapi.OneBasedPageableAsQueryParam;
import me.didk.common.response.ApiEnvelope;
import me.didk.common.response.PaginatedApiEnvelope;
import me.didk.wallet.dto.CreateDepositRequest;
import me.didk.wallet.dto.CreateWithdrawalRequest;
import me.didk.wallet.dto.DepositResponse;
import me.didk.wallet.dto.UpdateTransferStatusRequest;
import me.didk.wallet.dto.WithdrawalResponse;
import me.didk.wallet.service.DepositService;
import me.didk.wallet.service.WithdrawalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@Tag(name = "Transfers")
@RequestMapping("/api/v1/wallet")
public class TransfersController {
    private final DepositService depositService;
    private final WithdrawalService withdrawalService;

    public TransfersController(
            DepositService depositService,
            WithdrawalService withdrawalService
    ) {
        this.depositService = depositService;
        this.withdrawalService = withdrawalService;
    }

    @GetMapping("/deposits")
    @OneBasedPageableAsQueryParam
    public PaginatedApiEnvelope<List<DepositResponse>> deposits(
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(hidden = true)
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<DepositResponse> page = depositService.list(userId, pageable).map(DepositResponse::from);
        return PaginatedApiEnvelope.success("Success", page);
    }

    @PostMapping("/deposits")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiEnvelope<DepositResponse> createDeposit(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateDepositRequest request
    ) {
        return ApiEnvelope.success("Success", DepositResponse.from(depositService.create(userId, request)));
    }

    @GetMapping("/deposits/{id}")
    public ApiEnvelope<DepositResponse> depositById(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id
    ) {
        return ApiEnvelope.success("Success", DepositResponse.from(depositService.getById(userId, id)));
    }

    @PatchMapping("/deposits/{id}/status")
    public ApiEnvelope<DepositResponse> updateDepositStatus(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateTransferStatusRequest request
    ) {
        return ApiEnvelope.success(
                "Success",
                DepositResponse.from(depositService.updateStatus(userId, id, request.status(), request.txHash()))
        );
    }

    @PostMapping("/withdrawals")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiEnvelope<WithdrawalResponse> createWithdrawal(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CreateWithdrawalRequest request
    ) {
        return ApiEnvelope.success(
                "Success",
                WithdrawalResponse.from(withdrawalService.create(userId, idempotencyKey, request))
        );
    }

    @GetMapping("/withdrawals")
    @OneBasedPageableAsQueryParam
    public PaginatedApiEnvelope<List<WithdrawalResponse>> withdrawals(
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(hidden = true)
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<WithdrawalResponse> page = withdrawalService.list(userId, pageable).map(WithdrawalResponse::from);
        return PaginatedApiEnvelope.success("Success", page);
    }

    @GetMapping("/withdrawals/{id}")
    public ApiEnvelope<WithdrawalResponse> withdrawalById(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id
    ) {
        return ApiEnvelope.success("Success", WithdrawalResponse.from(withdrawalService.getById(userId, id)));
    }

    @PostMapping("/withdrawals/{id}/cancel")
    public ApiEnvelope<WithdrawalResponse> cancelWithdrawal(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id
    ) {
        return ApiEnvelope.success("Success", WithdrawalResponse.from(withdrawalService.cancel(userId, id)));
    }

    @PatchMapping("/withdrawals/{id}/status")
    public ApiEnvelope<WithdrawalResponse> updateWithdrawalStatus(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateTransferStatusRequest request
    ) {
        return ApiEnvelope.success(
                "Success",
                WithdrawalResponse.from(withdrawalService.updateStatus(userId, id, request.status(), request.errorMessage()))
        );
    }
}
