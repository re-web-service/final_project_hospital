package com.re.hospital.controllers;

import com.re.hospital.models.dtos.req.RegisterReq;
import com.re.hospital.models.dtos.res.ApiResponse;
import com.re.hospital.models.dtos.res.UserRes;
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
}