package com.nebula.studio.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nebula.studio.common.Result;
import com.nebula.studio.dto.request.*;
import com.nebula.studio.dto.response.LoginResponse;
import com.nebula.studio.entity.User;
import com.nebula.studio.security.JwtUserDetails;
import com.nebula.studio.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理与认证控制器
 *
 * <p>提供注册、登录、JWT令牌发放、用户信息查询、用户列表管理等接口。
 * 集成 Spring Security + JWT，支持 USER / DESIGNER / ADMIN 三种角色
 * 的路由级权限控制。</p>
 *
 * @author 姚朕言
 * @since 2026-05
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/auth/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(userService.register(request));
    }

    @PostMapping("/auth/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    @PostMapping("/auth/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @GetMapping("/user/profile")
    public Result<User> getProfile(@AuthenticationPrincipal JwtUserDetails userDetails) {
        return Result.success(userService.getProfile(userDetails.getUserId()));
    }

    @PutMapping("/user/profile")
    public Result<User> updateProfile(@AuthenticationPrincipal JwtUserDetails userDetails,
                                      @Valid @RequestBody UpdateUserRequest request) {
        return Result.success(userService.updateProfile(userDetails.getUserId(), request));
    }

    @PutMapping("/user/password")
    public Result<Void> changePassword(@AuthenticationPrincipal JwtUserDetails userDetails,
                                       @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userDetails.getUserId(), request);
        return Result.success();
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<User>> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role) {
        return Result.success(userService.listUsers(page, size, keyword, role));
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> getUserDetail(@PathVariable Long id) {
        return Result.success(userService.getProfile(id));
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        return Result.success(userService.createUser(request));
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return Result.success(userService.updateUser(id, request));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
}
