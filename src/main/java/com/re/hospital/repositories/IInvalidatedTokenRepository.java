package com.re.hospital.repositories;

import com.re.hospital.entities.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IInvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
    boolean existsById(String tokenId);
}
