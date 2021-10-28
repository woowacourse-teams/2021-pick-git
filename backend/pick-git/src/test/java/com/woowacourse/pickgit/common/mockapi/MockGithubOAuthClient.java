package com.woowacourse.pickgit.common.mockapi;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.common.fixture.TUser;

public class MockGithubOAuthClient implements OAuthClient {

    @Override
    public String getLoginUrl() {
        return "https://github.com/login/oauth/authorize?";
    }

    @Override
    public String getAccessToken(String code) {
        return "oauth.access.token." + code;
    }

    @Override
    public OAuthProfileResponse getGithubProfile(String githubAccessToken) {
        String[] splitToken = githubAccessToken.split("\\.");
        return TUser.oAuthProfileResponse(splitToken[3]);
    }
}
