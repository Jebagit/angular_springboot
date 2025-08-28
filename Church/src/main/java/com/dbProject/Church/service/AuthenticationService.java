package com.dbProject.Church.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

@Service
@Slf4j
public class AuthenticationService {
    
    public enum UserRole {
        SYSTEM_ADMINISTRATOR,
        ADMINISTRATOR,
        OPERATOR,
        USER
    }
    
    public static class TenantUserInfo {
        private final UUID tenantId;
        private final UserRole role;
        private final String userId;
        
        public TenantUserInfo(UUID tenantId, UserRole role, String userId) {
            this.tenantId = tenantId;
            this.role = role;
            this.userId = userId;
        }
        
        public UUID getTenantId() { return tenantId; }
        public UserRole getRole() { return role; }
        public String getUserId() { return userId; }
    }
    
    /**
     * Parse the x-tenant-user-token header and extract tenant ID and user role
     * For simplicity, assuming token format: base64(tenantId:role:userId)
     */
    public TenantUserInfo parseToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new IllegalArgumentException("Token is required");
            }
            
            String decoded = new String(Base64.getDecoder().decode(token));
            String[] parts = decoded.split(":");
            
            if (parts.length < 3) {
                throw new IllegalArgumentException("Invalid token format");
            }
            
            UUID tenantId = UUID.fromString(parts[0]);
            UserRole role = UserRole.valueOf(parts[1].toUpperCase());
            String userId = parts[2];
            
            return new TenantUserInfo(tenantId, role, userId);
            
        } catch (Exception e) {
            log.error("Failed to parse token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid token", e);
        }
    }
    
    /**
     * Check if the user has required role for administrative operations
     */
    public boolean hasAdminRole(UserRole role) {
        return role == UserRole.SYSTEM_ADMINISTRATOR || 
               role == UserRole.ADMINISTRATOR || 
               role == UserRole.OPERATOR;
    }
    
    /**
     * Check if the user is a global system administrator
     */
    public boolean isSystemAdministrator(UserRole role) {
        return role == UserRole.SYSTEM_ADMINISTRATOR;
    }
}