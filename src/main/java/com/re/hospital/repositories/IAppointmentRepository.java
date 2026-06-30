package com.re.hospital.repositories;

import com.re.hospital.entities.Appointment;
import com.re.hospital.entities.User;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface IAppointmentRepository extends JpaRepository<Appointment, Long> {
    // kiểm tra xem Bác sĩ đã có lịch hẹn trùng ngày và khung giờ chưa
    boolean existsByDoctorIdAndDateAndTimeSlot(Long doctorId, LocalDate date, String timeSlot);

    // tim kiem lich su kham cua benh nhan cu the
    List<Appointment> findByPatientOrderByDateDesc(User patient);
}
