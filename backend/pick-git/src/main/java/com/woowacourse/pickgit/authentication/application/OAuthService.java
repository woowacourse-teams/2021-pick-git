package com.woowacourse.pickgit.authentication.application;

import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import org.springframework.stereotype.Service;

@Service
public class OAuthService {

    private OAuthClient githubOAuthClient;

    public OAuthService(OAuthClient githubOAuthClient) {
        this.githubOAuthClient = githubOAuthClient;
    }

    public String getGithubAuthorizationUrl() {
        return githubOAuthClient.getLoginUrl();
    }

    public String createToken(String code) {
        String githubAccessToken = githubOAuthClient.getAccessToken(code);
        return githubAccessToken;
    }
}
