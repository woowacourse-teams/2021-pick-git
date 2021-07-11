package com.woowacourse.pickgit.authentication.application;

import com.woowacourse.pickgit.user.domain.User;

public interface JwtTokenProvider {

    String createToken(String payload);

    boolean validateToken(String token);

    String getPayloadByKey(String token, String key);
}
