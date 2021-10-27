package com.woowacourse.pickgit.unit.authentication.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.woowacourse.pickgit.authentication.application.JwtTokenProvider;
import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.application.dto.TokenDto;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.authentication.infrastructure.dao.CollectionOAuthAccessTokenDao;
import com.woowacourse.pickgit.exception.authentication.InvalidTokenException;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import com.woowacourse.pickgit.user.domain.search.UserSearchEngine;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OAuthServiceTest {

    private static final String GITHUB_CODE = "oauth authorization code";
    private static final String OAUTH_ACCESS_TOKEN = "oauth.access.token";
    private static final String JWT_TOKEN = "jwt token";

    @Mock
    private OAuthClient oAuthClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSearchEngine userSearchEngine;

    @Mock
    private CollectionOAuthAccessTokenDao oAuthAccessTokenDao;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private OAuthService oAuthService;

    @DisplayName("Github 로그인 URL을 반환하는데 성공한다.")
    @Test
    void getGithubAuthorizationUrl_Anonymous_ReturnGithubAuthorizationUrl() {
        // given
        String url = "https://github.com/login..";

        // mock
        given(oAuthClient.getLoginUrl()).willReturn(url);

        // then
        assertThat(oAuthService.getGithubAuthorizationUrl()).isEqualTo(url);
    }

    @DisplayName("회원가입(첫 로그인)시 Github Profile을 가져와서 DB에 insert한다.")
    @Test
    void createToken_Signup_SaveUserProfile() {
        // given
        OAuthProfileResponse githubProfileResponse = OAuthProfileResponse.builder()
            .name("name")
            .image("img.png")
            .description("bio")
            .githubUrl("www.github.com")
            .company("company")
            .location("location")
            .website("website")
            .twitter("twitter")
            .build();

        User user = new User(
            githubProfileResponse.toBasicProfile(),
            githubProfileResponse.toGithubProfile()
        );

        // mock
        given(oAuthClient.getAccessToken(anyString()))
            .willReturn(OAUTH_ACCESS_TOKEN);
        given(oAuthClient.getGithubProfile(anyString()))
            .willReturn(githubProfileResponse);
        given(userRepository.findByBasicProfile_Name(githubProfileResponse.getName()))
            .willReturn(Optional.empty());
        given(jwtTokenProvider.createToken(githubProfileResponse.getName()))
            .willReturn(JWT_TOKEN);

        // when
        TokenDto token = oAuthService.createToken(GITHUB_CODE);

        // then
        assertThat(token.getToken()).isEqualTo(JWT_TOKEN);
        verify(userRepository, times(1))
            .findByBasicProfile_Name(anyString());
        verify(userRepository, times(1))
            .save(any(User.class));
        verify(jwtTokenProvider, times(1))
            .createToken(anyString());
        verify(oAuthAccessTokenDao, times(1))
            .insert(anyString(), anyString());

        verify(userRepository, times(1))
            .findByBasicProfile_Name(githubProfileResponse.getName());
        verify(userRepository, times(1))
            .save(user);
        verify(userSearchEngine, times(1))
            .save(any());
        verify(jwtTokenProvider, times(1))
            .createToken(githubProfileResponse.getName());
        verify(oAuthAccessTokenDao, times(1))
            .insert(JWT_TOKEN, OAUTH_ACCESS_TOKEN);
    }

    @DisplayName("로그인(첫 로그인이 아닌경우)시 Github Profile을 가져와서 DB에 저장된 기존 정보를 update한다.")
    @Test
    void createToken_Signup_UpdateUserProfile() {
        // given
        OAuthProfileResponse githubProfileResponse = OAuthProfileResponse.builder()
            .name("name")
            .image("img.png")
            .description("bio")
            .githubUrl("www.github.com")
            .company("company")
            .location("location")
            .website("website")
            .twitter("twitter")
            .build();

        User user = new User(
            githubProfileResponse.toBasicProfile(),
            githubProfileResponse.toGithubProfile()
        );

        // mock
        given(oAuthClient.getAccessToken(anyString()))
            .willReturn(OAUTH_ACCESS_TOKEN);
        given(oAuthClient.getGithubProfile(anyString()))
            .willReturn(githubProfileResponse);
        given(userRepository.findByBasicProfile_Name(githubProfileResponse.getName()))
            .willReturn(Optional.of(user));
        given(jwtTokenProvider.createToken(githubProfileResponse.getName()))
            .willReturn(JWT_TOKEN);

        // when
        TokenDto token = oAuthService.createToken(GITHUB_CODE);

        // then
        assertThat(token.getToken()).isEqualTo(JWT_TOKEN);
        verify(userRepository, times(1))
            .findByBasicProfile_Name(anyString());
        verify(userRepository, never())
            .save(any(User.class));
        verify(jwtTokenProvider, times(1))
            .createToken(anyString());
        verify(oAuthAccessTokenDao, times(1))
            .insert(anyString(), anyString());

        verify(userRepository, times(1))
            .findByBasicProfile_Name(githubProfileResponse.getName());
        verify(userRepository, never())
            .save(user);
        verify(jwtTokenProvider, times(1))
            .createToken(githubProfileResponse.getName());
        verify(oAuthAccessTokenDao, times(1))
            .insert(JWT_TOKEN, OAUTH_ACCESS_TOKEN);
    }

    @DisplayName("JWT 토큰을 통해 AccessTokenDB에서 LoginUser에 대한 정보를 가져온다.")
    @Test
    void findRequestUserByToken_ValidToken_ReturnAppUser() {
        // given
        String username = "pick-git";

        // mock
        given(jwtTokenProvider.getPayloadByKey(JWT_TOKEN, "username"))
            .willReturn(username);
        given(oAuthAccessTokenDao.findByKeyToken(JWT_TOKEN))
            .willReturn(Optional.ofNullable(OAUTH_ACCESS_TOKEN));

        // when
        AppUser appUser = oAuthService.findRequestUserByToken(JWT_TOKEN);

        // then
        assertThat(appUser).isInstanceOf(LoginUser.class);
        assertThat(appUser.getUsername()).isEqualTo(username);
        assertThat(appUser.getAccessToken()).isEqualTo(OAUTH_ACCESS_TOKEN);
    }

    @DisplayName("AccessTokenDB에 저장되어 있지 않은 JWT 토큰이라면 예외가 발생한다.")
    @Test
    void findRequestUserByToken_NotFoundToken_ThrowException() {
        // given
        String notSavedToken = "not_saved_jwt_token";
        String username = "pick-git";

        // mock
        given(jwtTokenProvider.getPayloadByKey(notSavedToken, "username"))
            .willReturn(username);
        given(oAuthAccessTokenDao.findByKeyToken(notSavedToken))
            .willReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> oAuthService.findRequestUserByToken(notSavedToken))
            .isInstanceOf(InvalidTokenException.class)
            .hasFieldOrPropertyWithValue("errorCode", "A0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED)
            .hasMessage("토큰 인증 에러");
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
