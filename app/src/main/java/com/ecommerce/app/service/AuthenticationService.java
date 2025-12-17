package com.ecommerce.app.service;

import org.json.JSONObject;
import com.ecommerce.app.dto.RefreshTokenRequestDto;
import com.ecommerce.app.exception.EcomException;

public interface AuthenticationService {

    JSONObject authenticate(String mobile, String password) throws EcomException;

    JSONObject refreshToken(RefreshTokenRequestDto requestDto) throws EcomException;

}