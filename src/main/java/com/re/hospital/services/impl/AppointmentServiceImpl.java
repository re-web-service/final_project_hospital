package com.re.hospital.services.impl;

import com.re.hospital.entities.Appointment;
import com.re.hospital.entities.User;
import com.re.hospital.exceptions.HttpBadRequestException;
import com.re.hospital.exceptions.HttpNotFoundException;
import com.re.hospital.models.constants.AppointmentStatus;
import com.re.hospital.models.dtos.req.AppointmentReq;
import com.re.hospital.repositories.IAppointmentRepository;
import com.re.hospital.repositories.IUserRepository;
import com.re.hospital.services.IAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements IAppointmentService {

    private final IAppointmentRepository appointmentRepository;
    private final IUserRepository userRepository;

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
}