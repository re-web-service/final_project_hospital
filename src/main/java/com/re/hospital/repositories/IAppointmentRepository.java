package com.re.hospital.repositories;

import com.re.hospital.entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface IAppointmentRepository extends JpaRepository<Appointment, Long> {
    // kiểm tra xem Bác sĩ đã có lịch hẹn trùng ngày và khung giờ chưa
    boolean existsByDoctorIdAndDateAndTimeSlot(Long doctorId, LocalDate date, String timeSlot);
}
