package me.didk.user.service;

import me.didk.common.exception.ConflictException;
import me.didk.common.exception.NotFoundException;
import me.didk.user.domain.UserEntity;
import me.didk.user.domain.UserRole;
import me.didk.user.dto.CreateUserRequest;
import me.didk.user.dto.UpdateUserRequest;
import me.didk.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserEntity create(CreateUserRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("User with this email already exists");
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.email().trim().toLowerCase());
        user.setDisplayName(request.displayName().trim());
        user.setRole(request.role() == null ? UserRole.USER : request.role());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserEntity get(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public Page<UserEntity> list(String email, int page, int pageSize) {
        int safePageSize = Math.min(pageSize, 100);
        Pageable pageable = PageRequest.of(page - 1, safePageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (email == null || email.isBlank()) {
            return userRepository.findAll(pageable);
        }
        return userRepository.findAllByEmailContainingIgnoreCase(email.trim(), pageable);
    }

    @Transactional
    public UserEntity update(UUID id, UpdateUserRequest request) {
        UserEntity user = get(id);
        if (request.displayName() != null && !request.displayName().isBlank()) {
            user.setDisplayName(request.displayName().trim());
        }
        if (request.role() != null) {
            user.setRole(request.role());
        }
        return userRepository.save(user);
    }
}
