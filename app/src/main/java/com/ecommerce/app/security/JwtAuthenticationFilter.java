package com.ecommerce.app.security;

import java.io.IOException;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ecommerce.app.config.ApplicationConfig;
import com.ecommerce.app.exception.RestExceptionHandler;
import com.ecommerce.app.service.JwtService;
import com.ecommerce.app.utils.ApplicationConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LogManager.getLogger(JwtAuthenticationFilter.class);

	private final ApplicationConfig applicationConfig;
	private final RestExceptionHandler restExceptionHandler;
	private final ObjectMapper objectMapper;
	private final JwtService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {

			String authHeader = request.getHeader(ApplicationConstants.AUTHORIZATION);
			String apiKey = request.getHeader(applicationConfig.getEcommerceSecurityKey());

			if (isSwaggerRequest(request)) {
				filterChain.doFilter(request, response);

			} else if (authHeader != null && authHeader.startsWith(ApplicationConstants.BEARER)) {
				// JWT Token Authentication
				String token = authHeader.substring(7);
				
				if (jwtService.validateToken(token, jwtService.getAccessSignKey())) {
					String userId = jwtService.extractUserId(token, jwtService.getAccessSignKey());
					String mobile = jwtService.extractPhoneNumber(token, jwtService.getAccessSignKey());
					String role = jwtService.extractRole(token, jwtService.getAccessSignKey());
					
					if (userId != null && mobile != null && role != null) {
						SecurityContextHolder.getContext().setAuthentication(
							new TokenAuthentication(token, UUID.fromString(userId), mobile, role, 
								AuthorityUtils.NO_AUTHORITIES)
						);
						filterChain.doFilter(request, response);
					} else {
						throw new BadCredentialsException("Invalid token claims");
					}
				} else {
					throw new BadCredentialsException("Invalid or expired JWT token");
				}

			} else if (apiKey != null && !apiKey.isEmpty() && !apiKey.isBlank()
					&& apiKey.equals(applicationConfig.getEcommerceSecurityValue())) {
				// API Key Authentication
				SecurityContextHolder.getContext()
						.setAuthentication(new ApiKeyAuthentication(apiKey, null, AuthorityUtils.NO_AUTHORITIES));
				filterChain.doFilter(request, response);

			} else if (isPublicRequest(request)) {
				filterChain.doFilter(request, response);
			} else {
				throw new BadCredentialsException(
						"Full Authentication required to access this resource, please provide valid X-API-KEY or Bearer token.");
			}

		} catch (Exception ex) {
			LOGGER.error("Authentication error: {}", ex.getMessage());
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType(ApplicationConstants.APPLICATION_JSON);
			objectMapper.registerModule(new JavaTimeModule());
			response.getWriter().write(objectMapper.writeValueAsString(restExceptionHandler
					.exceptionHandler(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), HttpStatus.UNAUTHORIZED)));
		}
	}

	private boolean isSwaggerRequest(HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		return requestURI.contains("/swagger-ui/") || requestURI.contains("/v3/api-docs")
				|| requestURI.contains("/actuator");
	}

	private boolean isPublicRequest(HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		return requestURI.contains("/public") || requestURI.contains("/ws");
	}

}
