package com.re.hospital.services.impl;

import com.re.hospital.entities.RefreshToken;
import com.re.hospital.entities.InvalidatedToken;
import com.re.hospital.exceptions.*;
import com.re.hospital.models.dtos.req.*;
import com.re.hospital.models.dtos.res.*;
import com.re.hospital.repositories.*;
import com.re.hospital.security.CustomUserDetails;
import com.re.hospital.security.jwt.JwtUtils;
import com.re.hospital.services.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final IUserRepository userRepository;
    private final IRefreshTokenRepository refreshTokenRepository;
    private final IInvalidatedTokenRepository invalidatedTokenRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.refresh-expiration-ms:86400000}") // 24 giờ
    private Long refreshDurationMs;

    @Override
    @Transactional
    public JwtRes authenticateUser(LoginReq req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String jwt = jwtUtils.generateTokenFromUsername(userDetails.getUsername());

        com.re.hospital.entities.User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new HttpNotFoundException("User not found"));

        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshDurationMs))
                .build();
        refreshTokenRepository.save(refreshToken);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return JwtRes.builder()
                .token(jwt)
                .refreshToken(refreshToken.getToken())
                .username(userDetails.getUsername())
                .roles(roles)
                .build();
    }

    @Override
    @Transactional
    public TokenRefreshRes refreshAccessToken(TokenRefreshReq req) {
        RefreshToken token = refreshTokenRepository.findByToken(req.getRefreshToken())
                .orElseThrow(() -> new HttpBadRequestException("Refresh token is not in database!"));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new HttpBadRequestException("Refresh token was expired. Please make a new signin request");
        }

        String newAccessToken = jwtUtils.generateTokenFromUsername(token.getUser().getUsername());
        return new TokenRefreshRes(newAccessToken, token.getToken());
    }

    @Override
    @Transactional
    public void logoutUser(LogoutReq req) {
        String jwt = req.getToken();
        if (jwtUtils.validateJwtToken(jwt)) {
            String tokenId = jwtUtils.getTokenIdFromJwtToken(jwt);
            Date expiryTime = jwtUtils.getExpirationFromJwtToken(jwt);

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(tokenId)
                    .expiryTime(expiryTime)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken); // Đưa vào blacklist
        }
    }

    @Override
    @Transactional
    public void changePassword(String username, PasswordUpdateReq req) {
        // FIX TẠI ĐÂY: Sử dụng đầy đủ đường dẫn package
        com.re.hospital.entities.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new HttpNotFoundException("User not found"));

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPasswordHash())) {
            throw new HttpBadRequestException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPassword(String username) {
        com.re.hospital.entities.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new HttpNotFoundException("User not found"));

        // Đổi mật khẩu về mặc định khi yêu cầu Quên mật khẩu
        user.setPasswordHash(passwordEncoder.encode("Hospital@123"));
        userRepository.save(user);
    }
}