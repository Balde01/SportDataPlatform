package com.sportdataauth.security;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class InMemoryRefreshTokenServiceTest {

   private RefreshTokenService refreshTokenService;
   private UUID userId;

   @BeforeEach
   void setUp() {
       refreshTokenService = new InMemoryRefreshTokenService();
       userId = UUID.randomUUID();
   }

   @Test
   void shouldRotateRefreshToken() {
       String token = refreshTokenService.rotateRefreshToken(userId);
       assertNotNull(token);
       assertFalse(token.isBlank());
   }

   @Test
   void shouldInvalidateOldTokenAfterRotation() {
       String oldToken = refreshTokenService.rotateRefreshToken(userId);
       String newToken = refreshTokenService.rotateRefreshToken(userId);

       assertNotNull(oldToken);
       assertNotNull(newToken);
       assertNotEquals(oldToken, newToken);

       // old token should no longer validate
       assertNull(refreshTokenService.validateRefreshToken(oldToken));

       // new token should validate to the same user
       assertEquals(userId, refreshTokenService.validateRefreshToken(newToken));
   }

   @Test
   void shouldValidateRefreshToken() {
       String token = refreshTokenService.rotateRefreshToken(userId);

       UUID resolvedUserId = refreshTokenService.validateRefreshToken(token);

       assertEquals(userId, resolvedUserId);
   }

   @Test
   void shouldRevokeRefreshToken() {
       String token = refreshTokenService.rotateRefreshToken(userId);

       refreshTokenService.revoke(token);

       assertNull(refreshTokenService.validateRefreshToken(token));
   }

   @Test
   void shouldRevokeAllTokensForUser() {
       String token = refreshTokenService.rotateRefreshToken(userId);

       refreshTokenService.revokeAll(userId);

       assertNull(refreshTokenService.validateRefreshToken(token));
   }
}