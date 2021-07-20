package com.woowacourse.pickgit.authentication.domain;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;

public interface OAuthClient {

    String getLoginUrl();

    String getAccessToken(String code);

    OAuthProfileResponse getGithubProfile(String githubAccessToken);
}
