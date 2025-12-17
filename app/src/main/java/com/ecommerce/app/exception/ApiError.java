package com.ecommerce.app.exception;

import java.util.Date;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
class ApiError {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private Date timestamp;
	
	@Schema(description = "status")
	private HttpStatus status;
	
	@Schema(description = "data")
	private ErrorDataDto data;
}

@Data
@Builder
class ErrorDataDto {
	
	@Schema(description = "isError")
	private Boolean isError;
	
	@Schema(description = "errorResponse")
	private ErrorResponse errorResponse;
}

@Data
@Builder
class ErrorResponse {

	@Schema(description = "errorCode")
	private Integer errorCode;
	
	@Schema(description = "errorMessage")
	private String errorMessage;

}