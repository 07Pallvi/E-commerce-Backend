package com.ecommerce.app.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ecommerce.app.exception.ApiSuccess;
import com.ecommerce.app.exception.EcomException;
import com.ecommerce.app.security.ApiKeyAuthentication;
import com.ecommerce.app.security.TokenAuthentication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommonUtils {

    private CommonUtils() {
		throw new IllegalStateException("Utility class");
	}

	private static final Logger LOGGER = LogManager.getLogger(CommonUtils.class);

	private static final String MOBILE_REGEX = "^\\d{10}$";
	private static final String NAME_REGEX = "^[a-zA-Z]{1,50}$";
	private static final String NAME_SPACE_REGEX = "^[a-zA-Z\\s]+$";
	private static final String PINCODE_REGEX = "^[0-9]{6}$";
	private static final String EMAIL_REGEX = "^(?=.{1,40}$)[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$";


    public static ResponseEntity<Object> buildResponseEntity(ApiSuccess apiSuccess) {
		// LOGGER.info("Success: {}", apiSuccess);
		return new ResponseEntity<>(apiSuccess, apiSuccess.getStatus());
	}

    public static JSONObject createFinalJsonResponse(Object data) throws JSONException {
		JSONObject finalResponse = new JSONObject();
		finalResponse.put(ApplicationConstants.IS_ERROR, false);
		finalResponse.put(ApplicationConstants.SUCCESS_RESPONSE, data);
		return finalResponse;
	}

    public static void logException(Exception e, String message, String exceptionCase, JSONObject requestParameters) {
		try {
			JSONObject logData = new JSONObject();
			StackTraceElement[] stackTrace = e != null ? e.getStackTrace() : new StackTraceElement[0];

			logData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			logData.put("exceptionCase", exceptionCase);
			logData.put("message", message);
			logData.put("method", stackTrace.length > 0 ? stackTrace[0].getMethodName() : "N/A");
			logData.put("line", stackTrace.length > 0 ? String.valueOf(stackTrace[0].getLineNumber()) : "N/A");
			logData.put("requestParameters", requestParameters.toString());
			logData.put("className", stackTrace.length > 0 ? stackTrace[0].getClassName() : "N/A");
			logData.put("fileName", stackTrace.length > 0 ? stackTrace[0].getFileName() : "N/A");

			LOGGER.error("{}", logData.toString());

		} catch (JSONException ex) {
			LOGGER.error("Error creating JSON log: {}", ex.getMessage());
		}
	}

	public static void logException(String exceptionCase, String method, String className, JSONObject requestParameters) {
		try {
			JSONObject logData = new JSONObject();
			logData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			logData.put("exceptionCase", exceptionCase);
			logData.put("method", method);
			logData.put("class", className);
			logData.put("requestParameters", requestParameters.toString());

			LOGGER.error("{}", logData.toString());

		} catch (JSONException ex) {
			LOGGER.error("Error creating JSON log: {}", ex.getMessage());
		}
	}

	public static void validateMandatoryField(String value, String field) throws EcomException {
        if (value == null || value.isBlank()) {
            throw new EcomException(field + " is required", ErrorInfo.INVALID_REQUEST.getErrorCode());
        }
    }

    public static void validatemobile(String mobile, String field) throws EcomException {
		if (!mobile.matches(MOBILE_REGEX)) {
			throw new EcomException(field + " is invalid", ErrorInfo.INVALID_REQUEST.getErrorCode());
		}
    }

	public static String getRoleFromContext() {
	    String role = null;
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth instanceof TokenAuthentication tokenAuthentication) {
	        role = tokenAuthentication.getRole();
	    }
	    return role;
	}

	public static UUID getUserIdFromContext() {
	    UUID userId = null;
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth instanceof TokenAuthentication tokenAuthentication) {
	        userId = tokenAuthentication.getUserId();
	    } else if (auth instanceof ApiKeyAuthentication apiKeyAuthentication) {
	        userId = apiKeyAuthentication.getUserId();
	    }
	    return userId;
	}

	public static void validateName(String name) throws EcomException {
		if(!name.matches(NAME_REGEX)) {
			throw new EcomException("Name must contain only alphabets and be between 1 and 50 characters", 
			ErrorInfo.INVALID_REQUEST.getErrorCode());
		}
	}
	public static void validateEmail(String email, String field) throws EcomException {
		if(!email.matches(EMAIL_REGEX)){
			throw new EcomException(field + " is invalid", ErrorInfo.INVALID_REQUEST.getErrorCode());
		}
	}
	
	public static void validatePassword(String password) throws EcomException {
		if(password.length() < 6 || password.length() > 32) {
			throw new EcomException("Password must be between 6 and 32 characters", ErrorInfo.INVALID_REQUEST.getErrorCode());
		}
	}

	public static void validatePincode(String pinCode, String field) throws EcomException {
		if (!pinCode.matches(PINCODE_REGEX)) {
			throw new EcomException(field + " is invalid", ErrorInfo.INVALID_REQUEST.getErrorCode());
		}
	}

    public static void validateString(String value, String field, int maxLength) throws EcomException {
		if(!value.matches(NAME_SPACE_REGEX)) {
			throw new EcomException(field + " must contain only alphabets and space", ErrorInfo.INVALID_REQUEST.getErrorCode());
		}
        if(value.length() > maxLength) {
            throw new EcomException(field + " must be less than " + maxLength + " characters", ErrorInfo.INVALID_REQUEST.getErrorCode());
        }
    }
    
}
