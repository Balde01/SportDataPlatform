package com.sportdataauth.security;

import com.sportdataauth.domain.entity.User;
import com.sportdataauth.util.Clock;
import com.sportdataauth.util.TokenGenerator;

public class SimpleJwtService implements JwtService {

   private final TokenGenerator tokenGenerator;
   private final Clock clock;

   public SimpleJwtService(TokenGenerator tokenGenerator, Clock clock) {
       this.tokenGenerator = tokenGenerator;
       this.clock = clock;
   }

   @Override
   public JwtToken generateAccessToken(User user) {
       // MVP: not a real JWT yet, just a secure random token
       String token = tokenGenerator.generateToken();
       return new JwtToken(token, clock.now().plusMinutes(15));
   }

   @Override
   public boolean validateAccessToken(String token) {
       if (token == null) return false;

       String t = token.trim();
       if (t.isEmpty()) return false;

       // MVP: we only validate format/non-empty.
       // Real JWT validation would verify signature + expiration + claims.
       return true;
   }
}