package com.woowacourse.pickgit.common.mockapi;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;

public class MockGithubOAuthClient implements OAuthClient {

    @Override
    public String getLoginUrl() {
        return null;
    }

    @Override
    public String getAccessToken(String code) {
        return "Bearer testAccessToken";
    }

    @Override
    public OAuthProfileResponse getGithubProfile(String githubAccessToken) {
        return new OAuthProfileResponse(
            "testUser",
            "https://github.com/testImage.jpg",
            "testDescription",
            "https://github.com/bperhaps",
            "testCompany",
            "testLocation",
            "testWebsite",
            "testTwitter"
        );
    }
}
