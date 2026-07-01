package com.re.hospital.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.re.hospital.models.dtos.req.AppointmentReq;
import com.re.hospital.models.dtos.req.PasswordUpdateReq;
import com.re.hospital.models.dtos.res.AppointmentHistoryRes;
import com.re.hospital.security.SecurityConfig;
import com.re.hospital.security.jwt.JwtTokenFilter;
import com.re.hospital.security.jwt.JwtUtils;
import com.re.hospital.repositories.IInvalidatedTokenRepository;
import com.re.hospital.services.IAppointmentService;
import com.re.hospital.services.IAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@Import({SecurityConfig.class, JwtTokenFilter.class})
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IAppointmentService appointmentService;

    @MockitoBean
    private IAuthService authService;

    // Security Mocks to satisfy Context configuration
    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private IInvalidatedTokenRepository invalidatedTokenRepository;

    @Test
    @WithMockUser(username = "quocdat_patient")
    void createAppointment_Success() throws Exception {
        AppointmentReq req = new AppointmentReq();
        req.setDoctorId(2L);
        req.setDate(LocalDate.now().plusDays(1));
        req.setTimeSlot("09:00 - 10:00");
        req.setSymptomDescription("Fever");

        when(appointmentService.createAppointment(any(AppointmentReq.class), eq("quocdat_patient")))
                .thenReturn(100L);

        mockMvc.perform(post("/api/v1/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Appointment booked successfully"))
                .andExpect(jsonPath("$.data").value(100));
    }

    @Test
    @WithMockUser(username = "quocdat_patient")
    void getHistory_Success() throws Exception {
        AppointmentHistoryRes res = AppointmentHistoryRes.builder()
                .id(100L)
                .date(LocalDate.now())
                .timeSlot("09:00")
                .doctorName("doctor_test")
                .status("PENDING")
                .diagnosis("N/A")
                .build();

        when(appointmentService.getPatientAppointmentHistory("quocdat_patient"))
                .thenReturn(List.of(res));

        mockMvc.perform(get("/api/v1/patient/appointments/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Fetched history successfully"))
                .andExpect(jsonPath("$.data[0].id").value(100))
                .andExpect(jsonPath("$.data[0].doctorName").value("doctor_test"));
    }

    @Test
    @WithMockUser(username = "quocdat_patient")
    void changePassword_Success() throws Exception {
        PasswordUpdateReq req = new PasswordUpdateReq();
        req.setOldPassword("old_pass");
        req.setNewPassword("new_pass");

        doNothing().when(authService).changePassword(eq("quocdat_patient"), any(PasswordUpdateReq.class));

        mockMvc.perform(put("/api/v1/patient/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Password updated successfully"));

        verify(authService, times(1)).changePassword(eq("quocdat_patient"), any(PasswordUpdateReq.class));
    }
}
