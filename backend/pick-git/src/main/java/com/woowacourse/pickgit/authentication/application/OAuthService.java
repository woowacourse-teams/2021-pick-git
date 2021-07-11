package com.woowacourse.pickgit.authentication.application;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.dao.CollectionOAuthAccessTokenDao;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import org.springframework.stereotype.Service;

@Service
public class OAuthService {

    private OAuthClient githubOAuthClient;
    private JwtTokenProvider jwtTokenProvider;
    private CollectionOAuthAccessTokenDao authAccessTokenDao;
    private UserRepository userRepository;

    public OAuthService(OAuthClient githubOAuthClient,
        JwtTokenProvider jwtTokenProvider,
        CollectionOAuthAccessTokenDao authAccessTokenDao,
        UserRepository userRepository) {
        this.githubOAuthClient = githubOAuthClient;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authAccessTokenDao = authAccessTokenDao;
        this.userRepository = userRepository;
    }

    public String getGithubAuthorizationUrl() {
        return githubOAuthClient.getLoginUrl();
    }

    public String createToken(String code) {
        String githubAccessToken = githubOAuthClient.getAccessToken(code);

        OAuthProfileResponse githubProfileResponse = githubOAuthClient.getGithubProfile(githubAccessToken);

        updateUserOrCreateUser(githubProfileResponse);

        return createTokenAndSave(githubAccessToken, githubProfileResponse.getName());
    }

    private void updateUserOrCreateUser(OAuthProfileResponse githubProfileResponse) {
        GithubProfile latestGithubProfile = githubProfileResponse.toGithubProfile();

        userRepository.findByBasicProfile_Name(githubProfileResponse.getName())
            .ifPresentOrElse(user -> {
                user.changeGithubProfile(latestGithubProfile);
                userRepository.save(user);
            }, () -> {
                BasicProfile basicProfile = githubProfileResponse.toBasicProfile();
                User user = new User(basicProfile, latestGithubProfile);
                userRepository.save(user);
            });
    }

    private String createTokenAndSave(String githubAccessToken, String payload) {
        String token = jwtTokenProvider.createToken(payload);
        authAccessTokenDao.insert(token, githubAccessToken);
        return token;
    }

    public AppUser findRequestUserByToken(String authentication) {
        if (authentication == null) {
            return new GuestUser();
        }

        String username = jwtTokenProvider.getPayloadByKey(authentication, "username");
        String accessToken = authAccessTokenDao.findByKeyToken(authentication)
            .orElseThrow(() -> new IllegalArgumentException("다시 로그인해주세요."));
        return new LoginUser(username, accessToken);
    }

    public boolean validateToken(String authentication) {
        return jwtTokenProvider.validateToken(authentication);
    }
}
