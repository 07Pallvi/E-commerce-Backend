package com.ecommerce.app.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

import com.ecommerce.app.exception.ApiSuccess;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommonUtils {

    private CommonUtils() {
		throw new IllegalStateException("Utility class");
	}

	private static final Logger LOGGER = LogManager.getLogger(CommonUtils.class);


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
    
}
