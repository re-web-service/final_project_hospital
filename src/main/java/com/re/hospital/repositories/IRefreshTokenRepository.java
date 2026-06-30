package com.re.hospital.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.re.hospital.entities.User;
import com.re.hospital.entities.RefreshToken;
import org.springframework.data.jpa.repository.Modifying;
import java.util.Optional;

public interface IRefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUser(User user);
}
