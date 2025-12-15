package com.ecommerce.app.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.http.HttpStatus;
import java.util.Date;

@Data
public class ApiSuccess {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date timestamp;
    
	@Schema(description = "status")
	private HttpStatus status;
    
	@Schema(description = "data")
	private Object data;

    private ApiSuccess() {
        timestamp = new Date();
    }

    public ApiSuccess(HttpStatus status) {
        this();
        this.status = status;
    }

    public ApiSuccess(Object object, HttpStatus status) {
        this();
        this.status = status;
        this.data = object;
    }

}
