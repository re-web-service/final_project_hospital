package com.re.hospital.services;

import com.re.hospital.entities.MedicalRecord;
import com.re.hospital.models.dtos.req.AppointmentReq;
import com.re.hospital.models.dtos.req.MedicalRecordReq;
import com.re.hospital.models.dtos.res.AppointmentHistoryRes;

import java.util.List;

public interface IAppointmentService {
    // Đặt lịch khám (trả về ID lịch đã tạo thành công)
    Long createAppointment(AppointmentReq req, String patientUsername);

    List<AppointmentHistoryRes> getPatientAppointmentHistory(String patientUsername);
    void updateAppointmentStatus(Long id, String statusStr);
    void uploadMedicalRecord(MedicalRecordReq req);
}