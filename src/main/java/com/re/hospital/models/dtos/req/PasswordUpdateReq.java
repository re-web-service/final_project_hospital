package com.re.hospital.models.dtos.req;

import lombok.Data;

@Data
public class PasswordUpdateReq {
    private String oldPassword;
    private String newPassword;
}
