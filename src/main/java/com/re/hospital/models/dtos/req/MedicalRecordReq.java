package com.re.hospital.models.dtos.req;

import lombok.Data;

@Data
public class MedicalRecordReq {
    private Long appointmentId;
    private String diagnosis;
    private String treatmentPlan;
    private String fileUrl;
}
