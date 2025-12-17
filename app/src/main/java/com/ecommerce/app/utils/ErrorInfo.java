package com.ecommerce.app.utils;

public enum ErrorInfo {
	
	INVALID_REQUEST(1001, "Invalid request"),
    USER_NOT_FOUND(1002, "Invalid credentials"),
    INCORRECT_PASSWORD(1003, "Incorrect password"), 
	INVALID_MOBILE_NUMBER(1004, "Invalid mobile number"), 
	INVALID_HOST_HEADER(1005, "Invalid host header"), 
	USER_ALREADY_EXISTS(1006, "User already exists"), 
	ROLE_NOT_FOUND(1007, "Role not found"), 
	TOKEN_DOES_NOT_BELONG_TO_USER(1008, "Token does not belong to user"),
	USER_NOT_ACTIVE(1009, "User not active");


	private final Integer errorCode;
	private final String errorText;

	private ErrorInfo(Integer errorCode, String errorText) {
		this.errorCode = errorCode;
		this.errorText = errorText;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public String getErrorText() {
		return errorText;
	}
}
