package com.woowacourse.pickgit.common.mockapi;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.query.fixture.TUser;
import java.util.concurrent.TimeUnit;

public class MockGithubOAuthClient implements OAuthClient {

    @Override
    public String getLoginUrl() {
        return null;
    }

    @Override
    public String getAccessToken(String code) {
        return code;
    }

    @Override
    public OAuthProfileResponse getGithubProfile(String githubAccessToken) {
        return TUser.oAuthProfileResponse(githubAccessToken);
    }
}
