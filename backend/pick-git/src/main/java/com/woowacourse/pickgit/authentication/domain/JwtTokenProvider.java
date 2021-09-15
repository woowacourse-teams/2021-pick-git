package com.woowacourse.pickgit.authentication.domain;

import java.util.Map;

public interface JwtTokenProvider {

    String createToken(String payload);

    String createToken(Map<String, Object> payload);

    boolean validateToken(String token);

    String getPayloadByKey(String token, String key);

    void changeExpirationTime(long expirationTimeInMilliSeconds);
}
