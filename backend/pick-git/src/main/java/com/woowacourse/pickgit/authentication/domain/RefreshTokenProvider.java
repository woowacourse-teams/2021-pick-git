package com.woowacourse.pickgit.authentication.domain;

public interface RefreshTokenProvider {

    String issueRefreshToken(String username);

    String reissueAccessToken(String refreshToken);

    void setAccessTokenProvider(JwtTokenProvider accessTokenProvider);
}
