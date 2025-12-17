package com.ecommerce.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ecommerce.app.dto.RefreshTokenRequestDto;
import com.ecommerce.app.exception.ApiSuccess;
import com.ecommerce.app.exception.EcomException;
import com.ecommerce.app.service.AuthenticationService;
import com.ecommerce.app.utils.ApplicationConstants;
import com.ecommerce.app.utils.CommonUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/public/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthenticationController {

	private static final Logger LOGGER = LogManager.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = " Authenticate user -login and return JWT tokens")
    public ResponseEntity<Object> login(
        @Parameter(description = "mobile", example = "9176456734", required = true) @RequestParam String mobile,
		@Parameter(description = "password", example = "hjhjh@%hj", required = true) @RequestParam String password) throws EcomException {

		LOGGER.info("{} {}, {} login, {} mobile : {}, password : {}", 
        ApplicationConstants.CLASSNAME, this.getClass().getSimpleName(), ApplicationConstants.METHODNAME, ApplicationConstants.REQUESTPARAMETERS, 
        mobile, password);

        return CommonUtils.buildResponseEntity(new ApiSuccess(authenticationService.authenticate(mobile, password), HttpStatus.OK));
    }

    @PostMapping("/refresh/token")
    @Operation(summary = "Refresh access token", description = "Generate new access token using refresh token")
    public ResponseEntity<Object> refreshToken(
        @Parameter(description = "refreshToken", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", required = true) 
        @RequestBody RefreshTokenRequestDto requestDto) throws EcomException {

        LOGGER.info("{} {}, {} refreshToken, {} refreshToken : {} ", 
        ApplicationConstants.CLASSNAME, this.getClass().getSimpleName(), ApplicationConstants.METHODNAME, ApplicationConstants.REQUESTPARAMETERS, 
        requestDto.getRefreshToken());

        return CommonUtils.buildResponseEntity(new ApiSuccess( authenticationService.refreshToken(requestDto), HttpStatus.OK));
    }

    
}

