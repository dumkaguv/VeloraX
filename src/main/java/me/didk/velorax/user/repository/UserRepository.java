package me.didk.user.repository;

import me.didk.user.domain.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    boolean existsByEmailIgnoreCase(String email);

    Page<UserEntity> findAllByEmailContainingIgnoreCase(String email, Pageable pageable);
}
