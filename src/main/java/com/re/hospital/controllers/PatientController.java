package com.re.hospital.controllers;

import com.re.hospital.models.dtos.req.AppointmentReq;
import com.re.hospital.models.dtos.res.ApiResponse;
import com.re.hospital.services.IAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patient/appointments")
@RequiredArgsConstructor
public class PatientController {

    private final IAppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createAppointment(
            @RequestBody AppointmentReq req,
            @AuthenticationPrincipal UserDetails userDetails) {

        String mockUsername = "quocdat_patient";
        Long appointmentId = appointmentService.createAppointment(req, mockUsername);
//        Long appointmentId = appointmentService.createAppointment(req, userDetails.getUsername());
        ApiResponse<Long> response = ApiResponse.<Long>builder()
                .success(true)
                .message("Appointment booked successfully")
                .data(appointmentId)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}