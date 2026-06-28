package com.re.hospital.models.dtos.req;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AppointmentReq {
    private Long doctorId;
    private LocalDate date;
    private String timeSlot;
    private String symptomDescription;
}