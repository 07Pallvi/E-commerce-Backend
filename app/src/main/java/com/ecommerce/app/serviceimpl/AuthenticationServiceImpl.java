package com.ecommerce.app.serviceimpl;

import java.time.LocalDateTime;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.ecommerce.app.dto.RefreshTokenRequestDto;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.exception.EcomException;
import com.ecommerce.app.repository.UserRepository;
import com.ecommerce.app.service.AuthenticationService;
import com.ecommerce.app.service.JwtService;
import com.ecommerce.app.utils.CommonUtils;
import com.ecommerce.app.utils.ErrorInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger LOGGER = LogManager.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;


    @Override
    public JSONObject authenticate(String mobile, String password) throws EcomException {

        CommonUtils.validateMandatoryField(mobile, "Mobile number");
        CommonUtils.validateMandatoryField(password, "Password");
        CommonUtils.validateMobileNumber(mobile);

        User user = userRepository.findByMobileNumberAndIsActiveTrue(mobile).orElseThrow(() -> 
              new EcomException(ErrorInfo.USER_NOT_FOUND.getErrorText(), ErrorInfo.USER_NOT_FOUND.getErrorCode()));

        // For now, simple password comparison (INSECURE - replace with BCrypt in production)
        if (!user.getPassword().equals(password)) {
            throw new EcomException(ErrorInfo.INCORRECT_PASSWORD.getErrorText(), ErrorInfo.INCORRECT_PASSWORD.getErrorCode());
        }
        user.setLastLoggedIn(LocalDateTime.now());
        userRepository.save(user);

        String roleName = user.getRole().getName().name();
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getMobileNumber(), roleName);
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getMobileNumber());
        LOGGER.info("User authenticated successfully: {}", mobile);

        JSONObject response = new JSONObject();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("userId", user.getId());
        return CommonUtils.createFinalJsonResponse(response);
    }
    
    // Refresh access token using refresh token
    public JSONObject refreshToken(RefreshTokenRequestDto requestDto) throws EcomException {

        if(requestDto == null) {
            throw new EcomException(ErrorInfo.INVALID_REQUEST.getErrorText(), ErrorInfo.INVALID_REQUEST.getErrorCode());
        }
        String refreshToken = requestDto.getRefreshToken();
        CommonUtils.validateMandatoryField(refreshToken, "Refresh token");

        if (!jwtService.validateToken(refreshToken, jwtService.getRefreshSignKey())) {
            throw new EcomException("Invalid refresh token", ErrorInfo.INVALID_REQUEST.getErrorCode());
        }
        String userId = jwtService.extractUserId(refreshToken, jwtService.getRefreshSignKey());
        String mobileNumber = jwtService.extractPhoneNumber(refreshToken, jwtService.getRefreshSignKey());

        if (userId == null || mobileNumber == null) {
            throw new EcomException("Invalid refresh token claims", ErrorInfo.INVALID_REQUEST.getErrorCode());
        }
        User user = userRepository.findByIdAndIsActiveTrue(UUID.fromString(userId)).orElseThrow(() -> 
              new EcomException(ErrorInfo.USER_NOT_FOUND.getErrorText(), ErrorInfo.USER_NOT_FOUND.getErrorCode()));

        // Generate new access token
        String roleName = user.getRole().getName().name();
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getMobileNumber(), roleName);
        LOGGER.info("Access token refreshed for user: {}", mobileNumber);

        JSONObject response = new JSONObject();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("userId", user.getId());
        return CommonUtils.createFinalJsonResponse(response);
    }
}

