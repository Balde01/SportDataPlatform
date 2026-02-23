package com.sportdataauth.repository;

import java.time.Instant;

import com.sportdataauth.domain.entity.InvitationToken;

public interface InvitationTokenRepository {

   /**
    * Atomically:
    * - finds a token by hash
    * - verifies it is not used and not expired at "now"
    * - marks it as used (usedAt = now)
    * Returns the updated token, or null if invalid/expired/already used.
    */
   InvitationToken consumeValidByTokenHash(String tokenHash, Instant now);

   void save(InvitationToken token);
}