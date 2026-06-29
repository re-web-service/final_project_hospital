package com.re.hospital.controllers;

import com.re.hospital.models.dtos.req.UserSaveReq;
import com.re.hospital.models.dtos.res.ApiResponse;
import com.re.hospital.models.dtos.res.UserRes;
import com.re.hospital.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final IUserService userService;

    // Lấy danh sách kèm Tìm kiếm & Phân trang
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserRes>>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserRes> users = userService.getAllUsers(search, pageable);

        ApiResponse<Page<UserRes>> response = ApiResponse.<Page<UserRes>>builder()
                .success(true)
                .message("Fetched users successfully")
                .data(users)
                .build();
        return ResponseEntity.ok(response);
    }

    // Tạo mới tài khoản (Admin cấp quyền tùy chọn)
    @PostMapping
    public ResponseEntity<ApiResponse<UserRes>> createUser(@RequestBody UserSaveReq req) {
        UserRes createdUser = userService.createUser(req);
        ApiResponse<UserRes> response = ApiResponse.<UserRes>builder()
                .success(true)
                .message("User created successfully")
                .data(createdUser)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED); // 201 Created
    }

    // Cập nhật thông tin người dùng
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserRes>> updateUser(@PathVariable Long id, @RequestBody UserSaveReq req) {
        UserRes updatedUser = userService.updateUser(id, req);
        ApiResponse<UserRes> response = ApiResponse.<UserRes>builder()
                .success(true)
                .message("User updated successfully")
                .data(updatedUser)
                .build();
        return ResponseEntity.ok(response);
    }

    // Chuyển trạng thái vô hiệu hóa tài khoản (Soft Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteOrDeactivateUser(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("User deactivated successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}