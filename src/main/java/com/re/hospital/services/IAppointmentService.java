package com.re.hospital.services;

import com.re.hospital.models.dtos.req.AppointmentReq;

public interface IAppointmentService {
    // Đặt lịch khám (trả về ID lịch đã tạo thành công)
    Long createAppointment(AppointmentReq req, String patientUsername);
}