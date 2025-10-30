package com.onit.authentication.jwts.security.refresh;

import com.onit.authentication.jwts.modules.user.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    final RefreshTokenRepository refreshTokenRepository;

  /**
   * -- GETTER --
   * Método getter para o tempo de duração do refresh token
   */
  @Getter
  @Value("${jwt.jwtRefreshExpirationMs}")
    private long refreshTokenDurationMs;

    @Transactional
    public String createRefreshToken(User user) {
        // Remove old tokens if exists
        refreshTokenRepository.deleteByUser(user);

        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        final var now = Instant.now();
        final var expireDate = Instant.now().plusMillis(refreshTokenDurationMs);
        final var diff = expireDate.compareTo(now);

        log.info("Refresh Token Generated at {}, Expires at {}, and Diff time is {}",now,expireDate,diff);

        refreshTokenRepository.save(token);
        return token.getToken();
    }

    public boolean isTokenValid(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(rt -> rt.getExpiryDate().isAfter(Instant.now()))
                .orElse(false);
    }

    public User getUserFromRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(rt -> rt.getExpiryDate().isAfter(Instant.now()))
                .map(RefreshToken::getUser)
                .orElseThrow(null);
    }

    @Transactional
    public void revokeUserTokens(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

}
