package com.re.hospital.repositories;

import com.re.hospital.entities.Role;
import com.re.hospital.models.constants.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IRoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
