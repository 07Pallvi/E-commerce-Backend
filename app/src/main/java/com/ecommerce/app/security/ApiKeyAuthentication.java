package com.ecommerce.app.security;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class ApiKeyAuthentication extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 1L;

    private final String apiKey;
    
    private final UUID userId;

    public ApiKeyAuthentication(String apiKey, UUID userId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.apiKey = apiKey;
        this.userId = userId;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return apiKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ApiKeyAuthentication other = (ApiKeyAuthentication) obj;
        return Objects.equals(apiKey, other.apiKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiKey);
    }
    
    public UUID getUserId() {
    	return userId;
    }
}