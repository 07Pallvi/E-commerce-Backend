package com.ecommerce.app.exception;

import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(EcomException.class)
	public ResponseEntity<ApiError> handleEcomException(EcomException ex) {

		ErrorResponse errorResponse = ErrorResponse.builder()
				.errorCode(ex.getErrorCode())
				.errorMessage(ex.getErrorMessage())
				.build();

		ErrorDataDto dataDto = ErrorDataDto.builder()
				.isError(true)
				.errorResponse(errorResponse)
				.build();

		ApiError apiError = ApiError.builder()
				.timestamp(new Date())
				.status(HttpStatus.OK)
				.data(dataDto)
				.build();
		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(apiError);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGenericException(Exception ex) {
		ApiError apiError = exceptionHandler(10000, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.contentType(MediaType.APPLICATION_JSON)
				.body(apiError);
	}

	public ApiError exceptionHandler(int errorCode, String errorMessage, HttpStatus status) {
		ErrorResponse errorResponse = ErrorResponse.builder()
				.errorCode(errorCode)
				.errorMessage(errorMessage)
				.build();

		ErrorDataDto dataDto = ErrorDataDto.builder()
				.isError(true)
				.errorResponse(errorResponse)
				.build();

		return ApiError.builder()
				.timestamp(new Date())
				.status(status)
				.data(dataDto)
				.build();
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {

		StringBuilder errorStr = new StringBuilder("");

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();
			errorStr.append(errorMessage);
			errorStr.append(" ");
        });
		errorStr.trimToSize();
        ApiError apiError = exceptionHandler(10001, errorStr.toString(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(apiError);
    }

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleArgumentMismatchExceptions(MethodArgumentTypeMismatchException ex) {

		String errorMessage = ex.getMessage();
		String exceptionMessage;

		if(errorMessage.contains("java.time.LocalDate")){
			exceptionMessage = "Invalid date. Use the correct date format (YYYY-MM-DD).";
		}else{
			exceptionMessage = "Invalid input value. Please ensure all input values are correct.";
		}
		ApiError apiError = exceptionHandler(10001, exceptionMessage, HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.contentType(MediaType.APPLICATION_JSON)
				.body(apiError);
    }
	
}
