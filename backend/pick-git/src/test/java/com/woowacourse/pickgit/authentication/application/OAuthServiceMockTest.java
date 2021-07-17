package com.woowacourse.pickgit.authentication.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.woowacourse.pickgit.authentication.application.dto.TokenDto;
import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.dao.CollectionOAuthAccessTokenDao;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.exception.authentication.InvalidTokenException;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("OAuthService Mock 단위 테스트")
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OAuthServiceMockTest {

    @Mock
    private OAuthClient oAuthClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CollectionOAuthAccessTokenDao oAuthAccessTokenDao;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private OAuthService oAuthService;

    @DisplayName("Github 로그인 URL을 반환한다.")
    @Test
    void getGithubAuthorizationUrl_Anonymous_ReturnGithubAuthorizationUrl() {
        // given
        String url = "https://github.com/login..";

        // mock
        when(oAuthClient.getLoginUrl()).thenReturn(url);

        // then
        assertThat(oAuthService.getGithubAuthorizationUrl()).isEqualTo(url);
    }

    @DisplayName("회원가입(첫 로그인)시 Github Profile을 가져와서 DB에 insert한다.")
    @Test
    void createToken_Signup_SaveUserProfile() {
        // given
        String code = "oauth authorization code";
        String oauthAccessToken = "oauth access token";
        String jwtToken = "jwt token";

        OAuthProfileResponse githubProfileResponse = new OAuthProfileResponse();
        githubProfileResponse.setName("test");
        githubProfileResponse.setDescription("hi~");

        User user = new User(
            githubProfileResponse.toBasicProfile(),
            githubProfileResponse.toGithubProfile()
        );

        // mock
        when(oAuthClient.getAccessToken(code)).thenReturn(oauthAccessToken);
        when(oAuthClient.getGithubProfile(oauthAccessToken)).thenReturn(githubProfileResponse);
        when(userRepository.findByBasicProfile_Name(githubProfileResponse.getName())).thenReturn(
            Optional.empty());
        when(jwtTokenProvider.createToken(githubProfileResponse.getName())).thenReturn(jwtToken);

        // when
        TokenDto token = oAuthService.createToken(code);

        // then
        assertThat(token.getToken()).isEqualTo(jwtToken);
        verify(userRepository, times(1)).findByBasicProfile_Name(githubProfileResponse.getName());
        verify(userRepository, times(1)).save(user);
        verify(jwtTokenProvider, times(1)).createToken(githubProfileResponse.getName());
        verify(oAuthAccessTokenDao, times(1)).insert(jwtToken, oauthAccessToken);
    }

    @DisplayName("로그인(첫 로그인이 아닌경우)시 Github Profile을 가져와서 DB에 저장된 기존 정보를 update한다.")
    @Test
    void createToken_Signup_UpdateUserProfile() {
        // given
        String code = "oauth authorization code";
        String oauthAccessToken = "oauth access token";
        String jwtToken = "jwt token";

        OAuthProfileResponse githubProfileResponse = new OAuthProfileResponse();
        githubProfileResponse.setName("test");
        githubProfileResponse.setDescription("hi~");

        User user = new User(
            githubProfileResponse.toBasicProfile(),
            githubProfileResponse.toGithubProfile()
        );

        // mock
        when(oAuthClient.getAccessToken(code)).thenReturn(oauthAccessToken);
        when(oAuthClient.getGithubProfile(oauthAccessToken)).thenReturn(githubProfileResponse);
        when(userRepository.findByBasicProfile_Name(githubProfileResponse.getName())).thenReturn(
            Optional.of(user));
        when(jwtTokenProvider.createToken(githubProfileResponse.getName())).thenReturn(jwtToken);

        // when
        TokenDto token = oAuthService.createToken(code);

        // then
        assertThat(token.getToken()).isEqualTo(jwtToken);
        verify(userRepository, times(1)).findByBasicProfile_Name(githubProfileResponse.getName());
        verify(userRepository, never()).save(user);
        verify(jwtTokenProvider, times(1)).createToken(githubProfileResponse.getName());
        verify(oAuthAccessTokenDao, times(1)).insert(jwtToken, oauthAccessToken);
    }

    @DisplayName("JWT 토큰을 통해 AccessTokenDB에서 LoginUser에 대한 정보를 가져온다.")
    @Test
    void findRequestUserByToken_ValidToken_ReturnAppUser() {
        // given
        String token = "jwt token";
        String accessToken = "oauth access token";
        String username = "pick-git";

        // mock
        when(jwtTokenProvider.getPayloadByKey(token, "username")).thenReturn(username);
        when(oAuthAccessTokenDao.findByKeyToken(token)).thenReturn(Optional.ofNullable(accessToken));

        // when
        AppUser appUser = oAuthService.findRequestUserByToken(token);

        // then
        assertThat(appUser).isInstanceOf(LoginUser.class);
        assertThat(appUser.getUsername()).isEqualTo(username);
        assertThat(appUser.getAccessToken()).isEqualTo(accessToken);
    }

    @DisplayName("AccessTokenDB에 저장되어 있지 않은 JWT 토큰이라면 예외가 발생한다.")
    @Test
    void findRequestUserByToken_NotFoundToken_ThrowException() {
        // given
        String token = "never saved jwt token";
        String username = "pick-git";

        // mock
        when(jwtTokenProvider.getPayloadByKey(token, "username")).thenReturn(username);
        when(oAuthAccessTokenDao.findByKeyToken(token)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> oAuthService.findRequestUserByToken(token))
            .isInstanceOf(InvalidTokenException.class);
    }

    @DisplayName("빈 JWT 토큰이면 GuestUser를 반환한다.")
    @Test
    void findRequestUserByToken_EmptyToken_ReturnGuest() {
        // when
        AppUser appUser = oAuthService.findRequestUserByToken(null);

        // then
        assertThat(appUser).isInstanceOf(GuestUser.class);
    }
}
