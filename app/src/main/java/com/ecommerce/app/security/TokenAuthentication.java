package com.ecommerce.app.security;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class TokenAuthentication extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 1L;

    private final String token;
    
    private final UUID userId;

    private final String mobile;

    private final String role;

    public TokenAuthentication(String token, UUID userId, String mobile, String role, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.userId = userId;
        this.mobile = mobile;
        this.role = role;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        TokenAuthentication other = (TokenAuthentication) obj;
        return Objects.equals(token, other.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }
    
    public UUID getUserId() {
    	return userId;
    }

    public String getmobile() { return mobile; }

    public String getRole() { return role; }
}