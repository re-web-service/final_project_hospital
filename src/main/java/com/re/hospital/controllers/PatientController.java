package com.re.hospital.controllers;

import com.re.hospital.models.dtos.req.AppointmentReq;
import com.re.hospital.models.dtos.req.PasswordUpdateReq;
import com.re.hospital.models.dtos.res.ApiResponse;
import com.re.hospital.models.dtos.res.AppointmentHistoryRes;
import com.re.hospital.services.IAppointmentService;
import com.re.hospital.services.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patient")
@RequiredArgsConstructor
public class PatientController {

    private final IAppointmentService appointmentService;
    private final IAuthService authService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createAppointment(
            @RequestBody AppointmentReq req,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = (userDetails != null) ? userDetails.getUsername() : "quocdat_patient";
        Long appointmentId = appointmentService.createAppointment(req, username);
        ApiResponse<Long> response = ApiResponse.<Long>builder()
                .success(true)
                .message("Appointment booked successfully")
                .data(appointmentId)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/appointments/history")
    public ResponseEntity<ApiResponse<List<AppointmentHistoryRes>>> getHistory(@AuthenticationPrincipal UserDetails userDetails) {
        String username = (userDetails != null) ? userDetails.getUsername() : "quocdat_patient";
        List<AppointmentHistoryRes> history = appointmentService.getPatientAppointmentHistory(username);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fetched history successfully", history));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PasswordUpdateReq req) {
        authService.changePassword(userDetails.getUsername(), req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password updated successfully", null));
    }
}