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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private IAppointmentRepository appointmentRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IMedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private User patient;
    private User doctor;
    private AppointmentReq appointmentReq;

    @BeforeEach
    void setUp() {
        patient = User.builder()
                .id(1L)
                .username("patient_test")
                .isActive(true)
                .build();

        doctor = User.builder()
                .id(2L)
                .username("doctor_test")
                .isActive(true)
                .build();

        appointmentReq = new AppointmentReq();
        appointmentReq.setDoctorId(2L);
        appointmentReq.setDate(LocalDate.now().plusDays(1));
        appointmentReq.setTimeSlot("09:00 - 10:00");
        appointmentReq.setSymptomDescription("Headache");
    }

    @Test
    void createAppointment_Success() {
        when(userRepository.findByUsername("patient_test")).thenReturn(Optional.of(patient));
        when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.existsByDoctorIdAndDateAndTimeSlot(2L, appointmentReq.getDate(), appointmentReq.getTimeSlot()))
                .thenReturn(false);

        Appointment savedAppointment = Appointment.builder()
                .id(100L)
                .date(appointmentReq.getDate())
                .timeSlot(appointmentReq.getTimeSlot())
                .status(AppointmentStatus.PENDING)
                .patient(patient)
                .doctor(doctor)
                .build();

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        Long id = appointmentService.createAppointment(appointmentReq, "patient_test");

        assertEquals(100L, id);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void createAppointment_Conflict_ThrowsHttpBadRequestException() {
        when(userRepository.findByUsername("patient_test")).thenReturn(Optional.of(patient));
        when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.existsByDoctorIdAndDateAndTimeSlot(2L, appointmentReq.getDate(), appointmentReq.getTimeSlot()))
                .thenReturn(true);

        assertThrows(HttpBadRequestException.class, () -> 
                appointmentService.createAppointment(appointmentReq, "patient_test")
        );
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void createAppointment_PatientNotFound_ThrowsHttpNotFoundException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(HttpNotFoundException.class, () -> 
                appointmentService.createAppointment(appointmentReq, "unknown")
        );
    }

    @Test
    void getPatientAppointmentHistory_Success() {
        when(userRepository.findByUsername("patient_test")).thenReturn(Optional.of(patient));
        
        Appointment appointment = Appointment.builder()
                .id(100L)
                .date(LocalDate.now())
                .timeSlot("09:00")
                .doctor(doctor)
                .status(AppointmentStatus.PENDING)
                .build();

        when(appointmentRepository.findByPatientOrderByDateDesc(patient))
                .thenReturn(List.of(appointment));

        List<AppointmentHistoryRes> history = appointmentService.getPatientAppointmentHistory("patient_test");

        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals("PENDING", history.get(0).getStatus());
        assertEquals("doctor_test", history.get(0).getDoctorName());
    }

    @Test
    void uploadMedicalRecord_Success() {
        Appointment appointment = Appointment.builder()
                .id(100L)
                .status(AppointmentStatus.PENDING)
                .build();

        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(appointment));

        MedicalRecordReq recordReq = new MedicalRecordReq();
        recordReq.setAppointmentId(100L);
        recordReq.setDiagnosis("Common Cold");
        recordReq.setTreatmentPlan("Rest and drink water");
        recordReq.setFileUrl("http://example.com/file.pdf");

        appointmentService.uploadMedicalRecord(recordReq);

        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    void updateAppointmentStatus_Success() {
        Appointment appointment = Appointment.builder()
                .id(100L)
                .status(AppointmentStatus.PENDING)
                .build();

        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(appointment));

        appointmentService.updateAppointmentStatus(100L, "APPROVED");

        assertEquals(AppointmentStatus.APPROVED, appointment.getStatus());
        verify(appointmentRepository, times(1)).save(appointment);
    }
}
