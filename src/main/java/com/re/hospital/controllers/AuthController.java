package com.re.hospital.controllers;

import com.re.hospital.models.dtos.req.LoginReq;
import com.re.hospital.models.dtos.req.LogoutReq;
import com.re.hospital.models.dtos.req.RegisterReq;
import com.re.hospital.models.dtos.req.TokenRefreshReq;
import com.re.hospital.models.dtos.res.ApiResponse;
import com.re.hospital.models.dtos.res.JwtRes;
import com.re.hospital.models.dtos.res.TokenRefreshRes;
import com.re.hospital.models.dtos.res.UserRes;
import com.re.hospital.services.IAuthService;
import com.re.hospital.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserRes>> registerPatient(@RequestBody RegisterReq req) {
        UserRes registeredUser = userService.registerPatient(req);

        ApiResponse<UserRes> response = ApiResponse.<UserRes>builder()
                .success(true)
                .message("Patient registered successfully")
                .data(registeredUser)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtRes>> authenticateUser(@RequestBody LoginReq req) {
        JwtRes jwtRes = authService.authenticateUser(req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successfully", jwtRes));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenRefreshRes>> refreshAccessToken(@RequestBody TokenRefreshReq req) {
        TokenRefreshRes res = authService.refreshAccessToken(req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Token refreshed successfully", res));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logoutUser(@RequestBody LogoutReq req) {
        authService.logoutUser(req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Logout successfully", null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String username) {
        authService.resetPassword(username);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password has been reset to default: Hospital@123", null));
    }
}