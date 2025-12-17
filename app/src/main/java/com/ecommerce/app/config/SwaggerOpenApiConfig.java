package com.ecommerce.app.config;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;

@Configuration
@RequiredArgsConstructor
@SecuritySchemes({
    @SecurityScheme(name = "Bearer Authentication", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT" ),
    @SecurityScheme( name = "API Key Authentication", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER, paramName = "X-API-KEY" )
})
public class SwaggerOpenApiConfig {

    private final ApplicationConfig applicationConfig;

    @Bean
	OpenAPI springOpenAPI() {

		return new OpenAPI().info(new Info().title("E-Commerce APIs").description("E-Commerce APIs")
            .version("V.1").contact(new Contact().name("Pallvi")))
            .addServersItem(new Server().url(applicationConfig.getServerUrl()));
	}

    @Bean
	@Order
	OperationCustomizer addCustomHeaders() {
		return (operation, handlerMethod) -> {
			if (handlerMethod.getMethod().isAnnotationPresent(Operation.class)) {
				Operation annotation = handlerMethod.getMethod().getAnnotation(Operation.class);
				HeaderParameter apiKeyHeader = (HeaderParameter) new HeaderParameter().name("X-API-KEY")
                .required(annotation != null && annotation.security().length > 0)
				.example("22f6af7e-ce06-4534-a6b4-e902f09dc5d4");
                        
				operation.addParametersItem(apiKeyHeader);
			}
			return operation;
		};
	}
    
}
