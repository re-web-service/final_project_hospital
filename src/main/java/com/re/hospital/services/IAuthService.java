package com.re.hospital.services;

import com.re.hospital.models.dtos.req.*;
import com.re.hospital.models.dtos.res.*;

public interface IAuthService {
    JwtRes authenticateUser(LoginReq req);
    TokenRefreshRes refreshAccessToken(TokenRefreshReq req);
    void logoutUser(LogoutReq req);
    void changePassword(String username, PasswordUpdateReq req);
    void resetPassword(String username);
}
