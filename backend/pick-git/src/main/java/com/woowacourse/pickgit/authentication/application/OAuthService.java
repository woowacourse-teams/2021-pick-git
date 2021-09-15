package com.woowacourse.pickgit.authentication.application;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.application.dto.TokenDto;
import com.woowacourse.pickgit.authentication.domain.JwtTokenProvider;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.domain.RefreshTokenProvider;
import com.woowacourse.pickgit.authentication.domain.Token;
import com.woowacourse.pickgit.authentication.domain.TokenRepository;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.authentication.infrastructure.token.TokenBodyType;
import com.woowacourse.pickgit.exception.authentication.InvalidTokenException;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OAuthService {

    private OAuthClient githubOAuthClient;
    private JwtTokenProvider jwtTokenProvider;
    private RefreshTokenProvider refreshTokenProvider;
    private TokenRepository tokenRepository;
    private UserRepository userRepository;

    public OAuthService(OAuthClient githubOAuthClient,
        JwtTokenProvider jwtTokenProvider,
        RefreshTokenProvider refreshTokenProvider,
        TokenRepository tokenRepository,
        UserRepository userRepository) {
        this.githubOAuthClient = githubOAuthClient;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenProvider = refreshTokenProvider;
        this.refreshTokenProvider.setAccessTokenProvider(jwtTokenProvider);
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    public String getGithubAuthorizationUrl() {
        return githubOAuthClient.getLoginUrl();
    }

    @Transactional
    public TokenDto createToken(String code) {
        String githubAccessToken = githubOAuthClient.getAccessToken(code);

        OAuthProfileResponse githubProfileResponse = githubOAuthClient
            .getGithubProfile(githubAccessToken);

        updateUserOrCreateUser(githubProfileResponse);

        return new TokenDto(
            createTokenAndSave(
                githubAccessToken,
                githubProfileResponse.getName()
            ),
            githubProfileResponse.getName()
        );
    }

    private void updateUserOrCreateUser(OAuthProfileResponse githubProfileResponse) {
        GithubProfile latestGithubProfile = githubProfileResponse.toGithubProfile();

        userRepository.findByBasicProfile_Name(githubProfileResponse.getName())
            .ifPresentOrElse(user -> {
                user.changeGithubProfile(latestGithubProfile);
            }, () -> {
                BasicProfile basicProfile = githubProfileResponse.toBasicProfile();
                User user = new User(basicProfile, latestGithubProfile);
                userRepository.save(user);
            });
    }

    private String createTokenAndSave(String githubAccessToken, String username) {
        String refreshToken = refreshTokenProvider.issueRefreshToken(username);
        Token token = new Token(username, refreshToken, githubAccessToken);
        tokenRepository.save(token);

        return refreshTokenProvider.reissueAccessToken(refreshToken);
    }

    @Transactional(readOnly = true)
    public AppUser findRequestUserByToken(String authentication) {
        if (authentication == null) {
            return new GuestUser();
        }

        String username =
            jwtTokenProvider.getPayloadByKey(authentication, TokenBodyType.USERNAME.getValue());
        Token token = tokenRepository.findById(username)
            .orElseThrow(InvalidTokenException::new);
        String accessToken = token.getOauthToken();
        return new LoginUser(username, accessToken);
    }

    public boolean validateToken(String authentication) {
        return jwtTokenProvider.validateToken(authentication);
    }
}
