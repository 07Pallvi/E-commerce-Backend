package com.ecommerce.app.config;


import com.ecommerce.app.exception.RestExceptionHandler;
import com.ecommerce.app.utils.ApplicationConstants;
import com.ecommerce.app.utils.ErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCorsFilter implements Filter {

    private static final Logger LOGGER = LogManager.getLogger(SimpleCorsFilter.class);

    private Set<String> ALLOWED_HOSTS;
    
    private final RestExceptionHandler restExceptionHandler;
    
    @Value("${ecommerce.server.allowed.host.name}")
    private Set<String> hostName;
    
    @Value("${ecommerce.server.allowed.origin.name}")
    private Set<String> allowedOrigin;

    public SimpleCorsFilter(RestExceptionHandler restExceptionHandler) {
        LOGGER.info("SimpleCORSFilter init");
        this.restExceptionHandler = restExceptionHandler;
    }
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        ALLOWED_HOSTS = hostName != null ? hostName : Collections.emptySet();
        String host = request.getHeader(ApplicationConstants.HOST);

        LOGGER.info("####### HOST : {} ########",host);
        
        if (!isHostAllowed(host)) {
            handleInvalidHost(response);
            return;
        }

        String presentDomain = request.getHeader("Origin");
        LOGGER.info("ORIGIN : {}", presentDomain);

        if (allowedOrigin.contains(presentDomain)) {
            response.setHeader("Access-Control-Allow-Origin", presentDomain);
        }

        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "AUTH-TOKEN, Authorization, Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
        response.setHeader("Content-Security-Policy", "default-src 'self'");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        response.setHeader("X-XSS-Protection", "1; mode=block");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }

    private boolean isHostAllowed(String host) {
        return ALLOWED_HOSTS.contains(host);
    }

    private void handleInvalidHost(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(ApplicationConstants.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String errorResponse = objectMapper.writeValueAsString(
                restExceptionHandler.exceptionHandler(ErrorInfo.INVALID_HOST_HEADER.getErrorCode(),
                        ErrorInfo.INVALID_HOST_HEADER.getErrorText(), HttpStatus.OK));
        response.getWriter().write(errorResponse);
    }

}

