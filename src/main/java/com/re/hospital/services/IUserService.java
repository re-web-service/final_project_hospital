package com.re.hospital.services;

import com.re.hospital.models.dtos.req.RegisterReq;
import com.re.hospital.models.dtos.req.UserSaveReq;
import com.re.hospital.models.dtos.res.UserRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {
    // FR-04
    UserRes registerPatient(RegisterReq req);

    // FR-05
    Page<UserRes> getAllUsers(String search, Pageable pageable);
    UserRes createUser(UserSaveReq req);
    UserRes updateUser(Long id, UserSaveReq req);
    void deleteOrDeactivateUser(Long id);
}
