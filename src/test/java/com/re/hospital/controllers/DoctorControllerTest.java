package com.re.hospital.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.re.hospital.models.dtos.req.MedicalRecordReq;
import com.re.hospital.security.SecurityConfig;
import com.re.hospital.security.jwt.JwtTokenFilter;
import com.re.hospital.security.jwt.JwtUtils;
import com.re.hospital.repositories.IInvalidatedTokenRepository;
import com.re.hospital.services.IAppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorController.class)
@Import({SecurityConfig.class, JwtTokenFilter.class})
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IAppointmentService appointmentService;

    // Security Mocks to satisfy Context configuration
    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private IInvalidatedTokenRepository invalidatedTokenRepository;

    @Test
    @WithMockUser
    void changeStatus_Success() throws Exception {
        doNothing().when(appointmentService).updateAppointmentStatus(100L, "APPROVED");

        mockMvc.perform(patch("/api/v1/doctor/appointments/100")
                        .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Appointment status updated to APPROVED"));

        verify(appointmentService, times(1)).updateAppointmentStatus(100L, "APPROVED");
    }

    @Test
    @WithMockUser
    void createMedicalRecord_Success() throws Exception {
        MedicalRecordReq req = new MedicalRecordReq();
        req.setAppointmentId(100L);
        req.setDiagnosis("Flu");
        req.setTreatmentPlan("Pills");

        doNothing().when(appointmentService).uploadMedicalRecord(any(MedicalRecordReq.class));

        mockMvc.perform(post("/api/v1/doctor/medical-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Medical record created successfully"));

        verify(appointmentService, times(1)).uploadMedicalRecord(any(MedicalRecordReq.class));
    }
}
