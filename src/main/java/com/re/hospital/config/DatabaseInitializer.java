package com.re.hospital.config;

import com.re.hospital.entities.Role;
import com.re.hospital.entities.User;
import com.re.hospital.models.constants.RoleName;
import com.re.hospital.repositories.IRoleRepository;
import com.re.hospital.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final IRoleRepository roleRepository;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Khởi tạo danh sách Roles
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = Role.builder().name(roleName).build();
                roleRepository.save(role);
            }
        }

        // Lấy lại các role đã được đảm bảo tồn tại
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow();
        Role doctorRole = roleRepository.findByName(RoleName.ROLE_DOCTOR).orElseThrow();
        Role patientRole = roleRepository.findByName(RoleName.ROLE_PATIENT).orElseThrow();

        // 2. Khởi tạo tài khoản Admin mặc định
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .passwordHash(passwordEncoder.encode("Hospital@123"))
                    .isActive(true)
                    .roles(Set.of(adminRole))
                    .build();
            userRepository.save(admin);
        }

        // 3. Khởi tạo tài khoản Doctor mẫu
        if (userRepository.findByUsername("doctor1").isEmpty()) {
            User doctor = User.builder()
                    .username("doctor1")
                    .passwordHash(passwordEncoder.encode("Hospital@123"))
                    .isActive(true)
                    .roles(Set.of(doctorRole))
                    .build();
            userRepository.save(doctor);
        }

        // 4. Khởi tạo tài khoản Patient mẫu (Đồng bộ với mockUsername trong PatientController)
        if (userRepository.findByUsername("quocdat_patient").isEmpty()) {
            User patient = User.builder()
                    .username("quocdat_patient")
                    .passwordHash(passwordEncoder.encode("Hospital@123"))
                    .isActive(true)
                    .roles(Set.of(patientRole))
                    .build();
            userRepository.save(patient);
        }
    }
}
