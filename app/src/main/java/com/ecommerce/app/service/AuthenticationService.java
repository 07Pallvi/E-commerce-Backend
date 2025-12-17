package com.ecommerce.app.service;

import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import com.ecommerce.app.dto.RefreshTokenRequestDto;
import com.ecommerce.app.dto.SellerRegisterRequestDto;
import com.ecommerce.app.dto.UserRegisterRequestDto;
import com.ecommerce.app.exception.EcomException;

public interface AuthenticationService {

    JSONObject authenticate(String mobile, String password) throws EcomException;

    JSONObject refreshToken(RefreshTokenRequestDto requestDto) throws EcomException;

    JSONObject registerUser(UserRegisterRequestDto requestDto) throws EcomException;

    JSONObject validateToken(HttpHeaders headers) throws EcomException;

    JSONObject registerSeller(SellerRegisterRequestDto requestDto) throws EcomException;

}