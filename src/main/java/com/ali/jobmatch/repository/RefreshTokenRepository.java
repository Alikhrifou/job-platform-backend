package com.ali.jobmatch.repository;

import com.ali.jobmatch.entity.RefreshToken;
import com.ali.jobmatch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
    void deleteByExpiryDateBefore(Instant now);
}
