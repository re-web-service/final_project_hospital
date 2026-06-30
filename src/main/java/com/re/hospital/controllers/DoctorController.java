package com.re.hospital.controllers;

import com.re.hospital.models.dtos.req.MedicalRecordReq;
import com.re.hospital.models.dtos.res.ApiResponse;
import com.re.hospital.services.IAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final IAppointmentService appointmentService;

    // Phê duyệt / Từ chối lịch khám thông qua RequestParam (Ví dụ: ?status=APPROVED hoặc ?status=REJECTED)
    @PatchMapping("/appointments/{id}")
    public ResponseEntity<ApiResponse<Void>> changeStatus(@PathVariable Long id, @RequestParam String status) {
        appointmentService.updateAppointmentStatus(id, status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Appointment status updated to " + status, null));
    }

    // Tải lên và chốt Hồ sơ bệnh án
    @PostMapping("/medical-records")
    public ResponseEntity<ApiResponse<Void>> createMedicalRecord(@RequestBody MedicalRecordReq req) {
        appointmentService.uploadMedicalRecord(req);
        return new ResponseEntity<>(new ApiResponse<>(true, "Medical record created successfully", null), HttpStatus.CREATED);
    }
}
