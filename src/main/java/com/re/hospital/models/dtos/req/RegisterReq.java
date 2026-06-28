package com.re.hospital.models.dtos.req;

import lombok.Data;

@Data
public class RegisterReq {
    private String username;
    private String password;
}