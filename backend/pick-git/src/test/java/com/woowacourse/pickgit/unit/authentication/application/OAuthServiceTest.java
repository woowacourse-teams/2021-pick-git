package com.woowacourse.pickgit.unit.authentication.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.woowacourse.pickgit.authentication.application.OAuthService;
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
import com.woowacourse.pickgit.authentication.infrastructure.StringEncryptor;
import com.woowacourse.pickgit.exception.authentication.InvalidRefreshTokenException;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OAuthServiceTest {

    private static final String GITHUB_CODE = "oauth authorization code";
    private static final String OAUTH_ACCESS_TOKEN = "oauth access token";
    private static final String JWT_TOKEN = "jwt token";

    @Mock
    private OAuthClient oAuthClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenProvider refreshTokenProvider;

    @Mock
    private TokenRepository tokenRepository;

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
        given(oAuthClient.getAccessToken(GITHUB_CODE))
            .willReturn(OAUTH_ACCESS_TOKEN);
        given(oAuthClient.getGithubProfile(OAUTH_ACCESS_TOKEN))
            .willReturn(githubProfileResponse);
        given(userRepository.findByBasicProfile_Name(githubProfileResponse.getName()))
            .willReturn(Optional.empty());
        given(refreshTokenProvider.issueRefreshToken(githubProfileResponse.getName()))
            .willReturn("refreshToken");
        given(refreshTokenProvider.reissueAccessToken("refreshToken"))
            .willReturn(JWT_TOKEN);

        // when
        TokenDto token = oAuthService.createToken(GITHUB_CODE);

        // then
        assertThat(token.getToken()).isEqualTo(JWT_TOKEN);
        verify(userRepository, times(1))
            .findByBasicProfile_Name(githubProfileResponse.getName());
        verify(userRepository, times(1))
            .save(user);
        verify(refreshTokenProvider, times(1))
            .issueRefreshToken(githubProfileResponse.getName());
        verify(refreshTokenProvider, times(1))
            .reissueAccessToken("refreshToken");
        verify(tokenRepository, times(1))
            .save(any(Token.class));
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
        given(oAuthClient.getAccessToken(GITHUB_CODE))
            .willReturn(OAUTH_ACCESS_TOKEN);
        given(oAuthClient.getGithubProfile(OAUTH_ACCESS_TOKEN))
            .willReturn(githubProfileResponse);
        given(userRepository.findByBasicProfile_Name(githubProfileResponse.getName()))
            .willReturn(Optional.of(user));
        given(refreshTokenProvider.issueRefreshToken(githubProfileResponse.getName()))
            .willReturn("refreshToken");
        given(refreshTokenProvider.reissueAccessToken("refreshToken"))
            .willReturn(JWT_TOKEN);

        // when
        TokenDto token = oAuthService.createToken(GITHUB_CODE);

        // then
        assertThat(token.getToken()).isEqualTo(JWT_TOKEN);
        verify(userRepository, times(1))
            .findByBasicProfile_Name(githubProfileResponse.getName());
        verify(userRepository, never())
            .save(user);
        verify(refreshTokenProvider, times(1))
            .issueRefreshToken(githubProfileResponse.getName());
        verify(refreshTokenProvider, times(1))
            .reissueAccessToken("refreshToken");
        verify(tokenRepository, times(1))
            .save(any(Token.class));
    }

    @DisplayName("JWT 토큰을 통해 TokenDB에서 LoginUser에 대한 정보를 가져온다.")
    @Test
    void findRequestUserByToken_ValidToken_ReturnAppUser() {
        // given
        String username = "pick-git";

        // mock
        given(jwtTokenProvider.getPayloadByKey(JWT_TOKEN, "username"))
            .willReturn(username);
        given(tokenRepository.findById(StringEncryptor.encryptToSHA256(username)))
            .willReturn(Optional.ofNullable(
                new Token(username, "refreshtoken", OAUTH_ACCESS_TOKEN)
            ));

        // when
        AppUser appUser = oAuthService.findRequestUserByToken(JWT_TOKEN);

        // then
        assertThat(appUser).isInstanceOf(LoginUser.class);
        assertThat(appUser.getUsername()).isEqualTo(username);
        assertThat(appUser.getAccessToken()).isEqualTo(OAUTH_ACCESS_TOKEN);
    }

    @DisplayName("TokenDB에 저장되어 있지 않은 JWT 토큰이라면 예외가 발생한다.")
    @Test
    void findRequestUserByToken_NotFoundToken_ThrowException() {
        // given
        String notSavedToken = "not_saved_jwt_token";
        String username = "pick-git";

        // mock
        given(jwtTokenProvider.getPayloadByKey(notSavedToken, "username"))
            .willReturn(username);
        given(tokenRepository.findById(StringEncryptor.encryptToSHA256(username)))
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

    @DisplayName("Refresh Token에 접근하는 키를 이용해 RefreshToken을 찾고, AccessToken을 재발급한다.")
    @Test
    void reissueAccessToken_ValidKey_ReturnReissuedAccessToken() {
        // given
        String key = StringEncryptor.encryptToSHA256("hi~");

        // mock
        given(tokenRepository.findById(key))
            .willReturn(Optional.ofNullable(new Token(key, "refreshToken", "oauthToken")));
        given(refreshTokenProvider.reissueAccessToken("refreshToken"))
            .willReturn("reissuedAccessToken");

        // when
        String reissuedAccessToken = oAuthService.reissueAccessToken(key);

        // then
        assertThat(reissuedAccessToken).isEqualTo("reissuedAccessToken");
    }

    @DisplayName("Refresh Token을 접근하는 키가 없으면 401 예외가 발생한다.")
    @Test
    void reissueAccessToken_InValidKey_ReturnReissuedAccessToken() {
        // given
        String key = "";

        // mock
        given(tokenRepository.findById(anyString()))
            .willReturn(Optional.empty());

        // when, then
        assertThatCode(() -> {
            oAuthService.reissueAccessToken(key);
        }).isInstanceOf(InvalidRefreshTokenException.class);
    }

    @DisplayName("유효하지 않은 Refresh Token이면 401 예외가 발생한다.")
    @Test
    void reissueAccessToken_InvalidRefreshToken_ReturnReissuedAccessToken() {
        // given
        String key = StringEncryptor.encryptToSHA256("hi~");

        // mock
        given(tokenRepository.findById(key))
            .willReturn(Optional.ofNullable(new Token(key, "refreshToken", "oauthToken")));
        given(refreshTokenProvider.reissueAccessToken("refreshToken"))
            .willThrow(new InvalidRefreshTokenException());

        // when, then
        assertThatCode(() -> {
            oAuthService.reissueAccessToken(key);
        }).isInstanceOf(InvalidRefreshTokenException.class);
    }
}
