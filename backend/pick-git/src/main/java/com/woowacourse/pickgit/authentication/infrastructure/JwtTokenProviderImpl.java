package com.woowacourse.pickgit.authentication.infrastructure;

import com.woowacourse.pickgit.authentication.application.JwtTokenProvider;
import com.woowacourse.pickgit.exception.authentication.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProviderImpl implements JwtTokenProvider {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long expirationTimeInMilliSeconds;

    public JwtTokenProviderImpl() {
    }

    public JwtTokenProviderImpl(String secretKey, long expirationTimeInMilliSeconds) {
        this.secretKey = secretKey;
        this.expirationTimeInMilliSeconds = expirationTimeInMilliSeconds;
    }

    @Override
    public String createToken(String payload) {
        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + expirationTimeInMilliSeconds);

        return Jwts.builder()
            .claim("username", payload)
            .setIssuedAt(now)
            .setExpiration(expirationTime)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String getPayloadByKey(String token, String key) {
        try {
            return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get(key, String.class);
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }

    @Override
    public void changeExpirationTime(long expirationTimeInMilliSeconds) {
        this.expirationTimeInMilliSeconds = expirationTimeInMilliSeconds;
    }
}
