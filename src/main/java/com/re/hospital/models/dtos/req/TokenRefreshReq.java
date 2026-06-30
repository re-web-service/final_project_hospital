package com.re.hospital.models.dtos.req;

import lombok.Data;

@Data
public class TokenRefreshReq {
    private String refreshToken;
}
