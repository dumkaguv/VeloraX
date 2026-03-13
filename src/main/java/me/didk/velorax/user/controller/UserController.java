package me.didk.user.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.didk.common.openapi.OneBasedPageableAsQueryParam;
import me.didk.common.response.ApiEnvelope;
import me.didk.common.response.PaginatedApiEnvelope;
import me.didk.user.dto.CreateUserRequest;
import me.didk.user.dto.UpdateUserRequest;
import me.didk.user.dto.UserResponse;
import me.didk.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Users")
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiEnvelope<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return ApiEnvelope.success("Success", UserResponse.from(userService.create(request)));
    }

    @GetMapping
    @OneBasedPageableAsQueryParam
    public PaginatedApiEnvelope<List<UserResponse>> list(
            @RequestParam(required = false) String email,
            @Parameter(hidden = true) @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<UserResponse> users = userService.list(email, pageable).map(UserResponse::from);
        return PaginatedApiEnvelope.success("Success", users);
    }

    @GetMapping("/{id}")
    public ApiEnvelope<UserResponse> get(@PathVariable UUID id) {
        return ApiEnvelope.success("Success", UserResponse.from(userService.get(id)));
    }

    @PatchMapping("/{id}")
    public ApiEnvelope<UserResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        return ApiEnvelope.success("Success", UserResponse.from(userService.update(id, request)));
    }
}
