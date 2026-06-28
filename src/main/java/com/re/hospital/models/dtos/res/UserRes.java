package com.re.hospital.models.dtos.res;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class UserRes {
    private Long id;
    private String username;
    private Boolean isActive;
    private Set<String> roles;
}
