package com.re.hospital.services.impl;

import com.re.hospital.entities.Role;
import com.re.hospital.entities.User;
import com.re.hospital.exceptions.HttpBadRequestException;
import com.re.hospital.exceptions.HttpNotFoundException;
import com.re.hospital.models.constants.RoleName;
import com.re.hospital.models.dtos.req.RegisterReq;
import com.re.hospital.models.dtos.req.UserSaveReq;
import com.re.hospital.models.dtos.res.UserRes;
import com.re.hospital.repositories.IRoleRepository;
import com.re.hospital.repositories.IUserRepository;
import com.re.hospital.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder; // Định nghĩa trong SecurityConfig

    @Override
    @Transactional
    public UserRes registerPatient(RegisterReq req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new HttpBadRequestException("Username already exists!");
        }

        Role patientRole = roleRepository.findByName(RoleName.ROLE_PATIENT)
                .orElseThrow(() -> new HttpNotFoundException("Role PATIENT not found"));

        User user = User.builder()
                .username(req.getUsername())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .isActive(true)
                .roles(Set.of(patientRole))
                .build();

        User savedUser = userRepository.save(user);
        return mapToUserRes(savedUser);
    }

    @Override
    public Page<UserRes> getAllUsers(String search, Pageable pageable) {
        Page<User> userPage;
        if (search != null && !search.trim().isEmpty()) {
            userPage = userRepository.findByUsernameContainingIgnoreCase(search, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        // Ép buộc dùng Stream API
        return userPage.map(this::mapToUserRes);
    }

    @Override
    @Transactional
    public UserRes createUser(UserSaveReq req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new HttpBadRequestException("Username already exists!");
        }

        Set<Role> roles = getRolesFromReq(req.getRoles());

        User user = User.builder()
                .username(req.getUsername())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                .roles(roles)
                .build();

        return mapToUserRes(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserRes updateUser(Long id, UserSaveReq req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new HttpNotFoundException("User not found with id: " + id));

        user.setUsername(req.getUsername());
        if (req.getPassword() != null && !req.getPassword().trim().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        }
        if (req.getIsActive() != null) {
            user.setIsActive(req.getIsActive());
        }
        if (req.getRoles() != null && !req.getRoles().isEmpty()) {
            user.setRoles(getRolesFromReq(req.getRoles()));
        }

        return mapToUserRes(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteOrDeactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new HttpNotFoundException("User not found with id: " + id));
        // Đổi trạng thái hoặc xóa vật lý tùy ý. Ở đây chuyển Deactivate theo quy chuẩn an toàn
        user.setIsActive(false);
        userRepository.save(user);
    }

    // Gói hàm biến đổi thực thể sang DTO bằng Stream
    private UserRes mapToUserRes(User user) {
        return UserRes.builder()
                .id(user.getId())
                .username(user.getUsername())
                .isActive(user.getIsActive())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .build();
    }

    private Set<Role> getRolesFromReq(Set<RoleName> roleNames) {
        Set<Role> roles = new HashSet<>();
        if (roleNames != null) {
            roleNames.forEach(name -> {
                Role role = roleRepository.findByName(name)
                        .orElseThrow(() -> new HttpNotFoundException("Role not found: " + name));
                roles.add(role);
            });
        }
        return roles;
    }
}
