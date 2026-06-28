package com.re.hospital.repositories;

import com.re.hospital.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    // tìm kiếm theo username chứa từ khóa (Phân trang)
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}
