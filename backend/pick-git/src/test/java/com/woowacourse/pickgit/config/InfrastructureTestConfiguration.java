package com.woowacourse.pickgit.config;

import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.domain.TokenRepository;
import com.woowacourse.pickgit.common.mockapi.MockContributionApiRequester;
import com.woowacourse.pickgit.common.mockapi.MockGithubOAuthClient;
import com.woowacourse.pickgit.common.mockapi.MockPickGitProfileStorage;
import com.woowacourse.pickgit.common.mockapi.MockPickGitStorage;
import com.woowacourse.pickgit.common.mockapi.MockPlatformFollowingRequester;
import com.woowacourse.pickgit.common.mockapi.MockRepositoryApiRequester;
import com.woowacourse.pickgit.common.mockapi.MockTagApiRequester;
import com.woowacourse.pickgit.common.mockapi.MockTokenRepository;
import com.woowacourse.pickgit.post.domain.repository.PickGitStorage;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositoryApiRequester;
import com.woowacourse.pickgit.tag.infrastructure.PlatformTagApiRequester;
import com.woowacourse.pickgit.user.domain.follow.PlatformFollowingRequester;
import com.woowacourse.pickgit.user.domain.profile.PickGitProfileStorage;
import com.woowacourse.pickgit.user.infrastructure.requester.PlatformContributionApiRequester;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class InfrastructureTestConfiguration {

    @Bean
    public PlatformRepositoryApiRequester platformRepositoryApiRequester() {
        return new MockRepositoryApiRequester();
    }

    @Bean
    public PlatformTagApiRequester platformTagApiRequester() {
        return new MockTagApiRequester();
    }

    @Bean
    public PlatformContributionApiRequester platformContributionApiRequester() {
        return new MockContributionApiRequester();
    }

    @Bean
    public PlatformFollowingRequester platformFollowingRequester() {
        return new MockPlatformFollowingRequester();
    }

    @Bean
    public PickGitStorage pickGitStorage() {
        return new MockPickGitStorage();
    }

    @Bean
    public PickGitProfileStorage pickGitProfileStorage() {
        return new MockPickGitProfileStorage();
    }

    @Bean
    public OAuthClient githubOAuthClient() {
        return new MockGithubOAuthClient();
    }

    @Bean
    public TokenRepository tokenRepository() {
        return new MockTokenRepository();
    }
}
