package com.woowacourse.pickgit.authentication.application;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.application.dto.TokenDto;
import com.woowacourse.pickgit.authentication.domain.OAuthAccessTokenDao;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.exception.authentication.InvalidTokenException;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import com.woowacourse.pickgit.user.domain.search.UserSearchEngine;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OAuthService {

    private final OAuthClient githubOAuthClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuthAccessTokenDao authAccessTokenDao;
    private final UserRepository userRepository;
    private final UserSearchEngine userSearchEngine;

    @Transactional(propagation = Propagation.NEVER)
    public String getGithubAuthorizationUrl() {
        return githubOAuthClient.getLoginUrl();
    }

    @Transactional
    public TokenDto createToken(String code) {
        String githubAccessToken = githubOAuthClient.getAccessToken(code);

        OAuthProfileResponse githubProfileResponse =
            githubOAuthClient.getGithubProfile(githubAccessToken);

        updateUserOrCreateUser(githubProfileResponse);
        String token = createTokenAndSave(githubAccessToken, githubProfileResponse.getName());

        return new TokenDto(token, githubProfileResponse.getName());
    }

    private void updateUserOrCreateUser(OAuthProfileResponse githubProfileResponse) {
        GithubProfile latestGithubProfile = githubProfileResponse.toGithubProfile();

        userRepository.findByBasicProfile_Name(githubProfileResponse.getName())
            .ifPresentOrElse(user -> user.changeGithubProfile(latestGithubProfile),
                () -> {
                    BasicProfile basicProfile = githubProfileResponse.toBasicProfile();
                    User user = userRepository.save(new User(basicProfile, latestGithubProfile));
                    userSearchEngine.save(user);
                }
            );
    }

    private String createTokenAndSave(String githubAccessToken, String payload) {
        String token = jwtTokenProvider.createToken(payload);
        authAccessTokenDao.insert(token, githubAccessToken);
        return token;
    }

    public AppUser findRequestUserByToken(String authentication) {
        if (Objects.isNull(authentication)) {
            return new GuestUser();
        }

        String username = jwtTokenProvider.getPayloadByKey(authentication, "username");
        String accessToken = authAccessTokenDao.findByKeyToken(authentication)
            .orElseThrow(InvalidTokenException::new);
        return new LoginUser(username, accessToken);
    }

    @Transactional(propagation = Propagation.NEVER)
    public boolean validateToken(String authentication) {
        return jwtTokenProvider.validateToken(authentication);
    }
}
