package com.dbProject.Church.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService();
    }

    @Test
    void testParseValidToken() {
        // Arrange
        UUID tenantId = UUID.randomUUID();
        String token = Base64.getEncoder().encodeToString(
            (tenantId + ":ADMINISTRATOR:user123").getBytes()
        );

        // Act
        AuthenticationService.TenantUserInfo userInfo = authenticationService.parseToken(token);

        // Assert
        assertEquals(tenantId, userInfo.getTenantId());
        assertEquals(AuthenticationService.UserRole.ADMINISTRATOR, userInfo.getRole());
        assertEquals("user123", userInfo.getUserId());
    }

    @Test
    void testParseInvalidToken() {
        // Arrange
        String invalidToken = "invalid-token";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.parseToken(invalidToken);
        });
    }

    @Test
    void testParseNullToken() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.parseToken(null);
        });
    }

    @Test
    void testHasAdminRole() {
        // Assert
        assertTrue(authenticationService.hasAdminRole(AuthenticationService.UserRole.SYSTEM_ADMINISTRATOR));
        assertTrue(authenticationService.hasAdminRole(AuthenticationService.UserRole.ADMINISTRATOR));
        assertTrue(authenticationService.hasAdminRole(AuthenticationService.UserRole.OPERATOR));
        assertFalse(authenticationService.hasAdminRole(AuthenticationService.UserRole.USER));
    }

    @Test
    void testIsSystemAdministrator() {
        // Assert
        assertTrue(authenticationService.isSystemAdministrator(AuthenticationService.UserRole.SYSTEM_ADMINISTRATOR));
        assertFalse(authenticationService.isSystemAdministrator(AuthenticationService.UserRole.ADMINISTRATOR));
        assertFalse(authenticationService.isSystemAdministrator(AuthenticationService.UserRole.OPERATOR));
        assertFalse(authenticationService.isSystemAdministrator(AuthenticationService.UserRole.USER));
    }
}