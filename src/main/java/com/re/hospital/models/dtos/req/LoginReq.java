package com.re.hospital.models.dtos.req;

import lombok.Data;

@Data
public class LoginReq {
    private String username;
    private String password;
}
