package com.re.hospital.models.dtos.req;

import com.re.hospital.models.constants.RoleName;
import lombok.Data;
import java.util.Set;

@Data
public class UserSaveReq {
    private String username;
    private String password;
    private Boolean isActive;
    private Set<RoleName> roles;
}
