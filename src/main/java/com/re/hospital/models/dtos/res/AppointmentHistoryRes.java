package com.re.hospital.models.dtos.res;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AppointmentHistoryRes {
    private Long id;
    private LocalDate date;
    private String timeSlot;
    private String doctorName;
    private String status;
    private String diagnosis;
    private String fileUrl;
}
