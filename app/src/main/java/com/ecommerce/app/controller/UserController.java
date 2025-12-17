package com.ecommerce.app.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.app.entity.User;
import com.ecommerce.app.exception.ApiSuccess;
import com.ecommerce.app.exception.EcomException;
import com.ecommerce.app.repository.UserRepository;
import com.ecommerce.app.security.TokenAuthentication;
import com.ecommerce.app.utils.CommonUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management APIs (Protected)")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Get current authenticated user's profile")
    public ResponseEntity<Object> getProfile() throws EcomException {
        // Extract user information from security context
        TokenAuthentication auth = (TokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
        
        UUID userId = auth.getUserId();
        String role = auth.getRole();
        String mobileNumber = auth.getMobileNumber();

        // Fetch user from database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EcomException("User not found", HttpStatus.NOT_FOUND.value()));

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("mobileNumber", user.getMobileNumber());
        response.put("fullName", user.getFullName());
        response.put("email", user.getEmail());
        response.put("role", role);
        response.put("isActive", user.isActive());
        response.put("addressUpdated", user.isAddressUpdated());
        response.put("lastLoggedIn", user.getLastLoggedIn());

        return CommonUtils.buildResponseEntity(new ApiSuccess(response, HttpStatus.OK));
    }

    @GetMapping("/info")
    @Operation(summary = "Get user info", description = "Get basic user information from JWT token")
    public ResponseEntity<Object> getUserInfo() {
        // This endpoint demonstrates accessing JWT claims without database query
        TokenAuthentication auth = (TokenAuthentication) SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", auth.getUserId());
        userInfo.put("mobileNumber", auth.getMobileNumber());
        userInfo.put("role", auth.getRole());
        userInfo.put("authenticated", auth.isAuthenticated());

        return CommonUtils.buildResponseEntity(new ApiSuccess(userInfo, HttpStatus.OK));
    }
}

