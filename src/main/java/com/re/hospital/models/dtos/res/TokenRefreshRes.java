package com.re.hospital.models.dtos.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRefreshRes {
    private String accessToken;
    private String refreshToken;
}
