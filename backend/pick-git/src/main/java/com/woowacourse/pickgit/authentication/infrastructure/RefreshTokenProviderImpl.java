package com.woowacourse.pickgit.authentication.infrastructure;

import com.woowacourse.pickgit.authentication.domain.JwtTokenProvider;
import com.woowacourse.pickgit.authentication.domain.RefreshTokenProvider;
import com.woowacourse.pickgit.authentication.infrastructure.token.TokenBodyType;
import com.woowacourse.pickgit.exception.authentication.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenProviderImpl implements RefreshTokenProvider {

    @Value("${security.jwt.refresh.secret-key}")
    private String secretKey;
    @Value("${security.jwt.refresh.expiration-time}")
    private long expirationTimeInMilliSeconds;
    private JwtTokenProvider accessTokenProvider;

    public RefreshTokenProviderImpl() {
    }

    public RefreshTokenProviderImpl(String secretKey, long expirationTimeInMilliSeconds,
        JwtTokenProvider accessTokenProvider) {
        this.secretKey = secretKey;
        this.expirationTimeInMilliSeconds = expirationTimeInMilliSeconds;
        this.accessTokenProvider = accessTokenProvider;
    }

    @Override
    public String issueRefreshToken(String username) {
        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + expirationTimeInMilliSeconds);

        validateRefreshTokenPayload(username);

        Map<String, Object> payload = new HashMap<>();
        payload.put(TokenBodyType.USERNAME.getValue(), username);

        return Jwts.builder()
            .setClaims(payload)
            .setIssuedAt(now)
            .setExpiration(expirationTime)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    private void validateRefreshTokenPayload(String username) {
        if (Objects.isNull(username) || username.isBlank()) {
            throw new InvalidTokenException();
        }
    }

    @Override
    public String reissueAccessToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new InvalidTokenException();
        }

        Claims payloads = parsePayloads(refreshToken);
        String username = payloads.get(TokenBodyType.USERNAME.getValue(), String.class);

        return accessTokenProvider.createToken(username);
    }

    private boolean validateToken(String refreshToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parsePayloads(String refreshToken) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken).getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }

    @Override
    public void setAccessTokenProvider(JwtTokenProvider accessTokenProvider) {
        this.accessTokenProvider = accessTokenProvider;
    }
}
