package com.woowacourse.pickgit.authentication.domain;

public interface JwtTokenProvider {

    String createToken(String payload);

    boolean validateToken(String token);

    String getPayloadByKey(String token, String key);

    void changeExpirationTime(long expirationTimeInMilliSeconds);
}
