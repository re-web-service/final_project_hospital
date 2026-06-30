package com.re.hospital.models.dtos.res;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JwtRes {
    private String token;
    private String refreshToken;
    private String username;
    private List<String> roles;
}
