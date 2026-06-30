package com.re.hospital.services.impl;

import com.re.hospital.entities.Appointment;
import com.re.hospital.entities.MedicalRecord;
import com.re.hospital.entities.User;
import com.re.hospital.exceptions.HttpBadRequestException;
import com.re.hospital.exceptions.HttpNotFoundException;
import com.re.hospital.models.constants.AppointmentStatus;
import com.re.hospital.models.dtos.req.AppointmentReq;
import com.re.hospital.models.dtos.req.MedicalRecordReq;
import com.re.hospital.models.dtos.res.AppointmentHistoryRes;
import com.re.hospital.repositories.IAppointmentRepository;
import com.re.hospital.repositories.IMedicalRecordRepository;
import com.re.hospital.repositories.IUserRepository;
import com.re.hospital.services.IAppointmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements IAppointmentService {

    private final IAppointmentRepository appointmentRepository;
    private final IUserRepository userRepository;
    private final IMedicalRecordRepository medicalRecordRepository;

    @Override
    @Transactional
    public Long createAppointment(AppointmentReq req, String patientUsername) {
        // 1. Tìm thông tin bệnh nhân đang đăng nhập hệ thống
        User patient = userRepository.findByUsername(patientUsername)
                .orElseThrow(() -> new HttpNotFoundException("Patient context profile not found"));

        // 2. Tìm thông tin bác sĩ được chọn
        User doctor = userRepository.findById(req.getDoctorId())
                .orElseThrow(() -> new HttpNotFoundException("Doctor not found with id: " + req.getDoctorId()));

        // 3. Kiểm tra tính hợp lệ: Tránh trùng lịch (Ném Exception 400 Bad Request/Conflict)
        boolean isConflict = appointmentRepository.existsByDoctorIdAndDateAndTimeSlot(
                req.getDoctorId(), req.getDate(), req.getTimeSlot()
        );
        if (isConflict) {
            throw new HttpBadRequestException("Doctor is already booked for this date and time slot!");
        }

        // 4. Khởi tạo lịch khám với trạng thái mặc định PENDING
        Appointment appointment = Appointment.builder()
                .date(req.getDate())
                .timeSlot(req.getTimeSlot())
                .status(AppointmentStatus.PENDING)
                .symptomDescription(req.getSymptomDescription())
                .patient(patient)
                .doctor(doctor)
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        return saved.getId();
    }

    public List<AppointmentHistoryRes> getPatientAppointmentHistory(String patientUsername) {
        User patient = userRepository.findByUsername(patientUsername)
                .orElseThrow(() -> new HttpNotFoundException("Patient not found"));

        // Thực hiện truy vấn danh sách bằng Stream API chuẩn cấu trúc quy định
        return appointmentRepository.findByPatientOrderByDateDesc(patient).stream()
                .map(app -> {
                    // Lấy thông tin bệnh án nếu có thông qua mối quan hệ 1-1 bắc cầu
                    MedicalRecord mr = app.getMedicalRecord();
                    return AppointmentHistoryRes.builder()
                            .id(app.getId())
                            .date(app.getDate())
                            .timeSlot(app.getTimeSlot())
                            .doctorName(app.getDoctor().getUsername())
                            .status(app.getStatus().name())
                            .diagnosis(mr != null ? mr.getDiagnosis() : "N/A")
                            .fileUrl(mr != null ? mr.getFileUrl() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateAppointmentStatus(Long id, String statusStr) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new HttpNotFoundException("Appointment not found"));

        try {
            AppointmentStatus status = AppointmentStatus.valueOf(statusStr.toUpperCase());
            appointment.setStatus(status);
            appointmentRepository.save(appointment);
        } catch (IllegalArgumentException e) {
            throw new HttpBadRequestException("Invalid status value");
        }
    }

    @Transactional
    public void uploadMedicalRecord(MedicalRecordReq req) {
        Appointment appointment = appointmentRepository.findById(req.getAppointmentId())
                .orElseThrow(() -> new HttpNotFoundException("Appointment not found"));

        MedicalRecord record = MedicalRecord.builder()
                .appointment(appointment)
                .diagnosis(req.getDiagnosis())
                .treatmentPlan(req.getTreatmentPlan())
                .fileUrl(req.getFileUrl())
                .createdAt(java.time.LocalDateTime.now())
                .build();

        medicalRecordRepository.save(record);

        // Tự động chuyển đổi trạng thái cuộc hẹn thành COMPLETED sau khi lên hồ sơ bệnh án thành công
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
    }
}