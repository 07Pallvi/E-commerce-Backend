package com.ecommerce.app.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EcomException extends Exception {

    private static final long serialVersionUID = 1L;
	
    @Schema(description = "errorMessage")
    private final String errorMessage;
    
	@Schema(description = "errorCode")
	private final Integer errorCode;

    public EcomException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = null;
    }
}