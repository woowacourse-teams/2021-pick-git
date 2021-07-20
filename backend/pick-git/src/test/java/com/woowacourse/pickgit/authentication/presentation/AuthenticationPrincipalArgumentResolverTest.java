package com.woowacourse.pickgit.authentication.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.woowacourse.pickgit.authentication.application.JwtTokenProvider;
import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.dao.CollectionOAuthAccessTokenDao;
import com.woowacourse.pickgit.authentication.dao.OAuthAccessTokenDao;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.infrastructure.JwtTokenProviderImpl;
import com.woowacourse.pickgit.authentication.presentation.interceptor.AuthHeader;
import com.woowacourse.pickgit.exception.authentication.InvalidTokenException;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.ServletWebRequest;

@ExtendWith(MockitoExtension.class)
class AuthenticationPrincipalArgumentResolverTest {

    private JwtTokenProvider jwtTokenProvider;

    private OAuthAccessTokenDao oAuthAccessTokenDao;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private OAuthService oAuthService;

    private AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProviderImpl("pick-git", 3600000);
        oAuthAccessTokenDao = new CollectionOAuthAccessTokenDao();
        oAuthService = new OAuthService(null, jwtTokenProvider, oAuthAccessTokenDao, null);
        authenticationPrincipalArgumentResolver = new AuthenticationPrincipalArgumentResolver(oAuthService);
    }

    @DisplayName("유효한 토큰이면 LoginUser를 반환한다.")
    @Test
    void resolveArgument_ValidUserToken_ReturnLoginUser() throws Exception {
        // given
        String username = "pick-git";
        String accessToken = "oauth access token";
        String jwtToken = jwtTokenProvider.createToken(username);

        oAuthAccessTokenDao.insert(jwtToken, accessToken);

        // mock
        when(httpServletRequest.getAttribute(AuthHeader.AUTHENTICATION)).thenReturn(jwtToken);

        // when
        AppUser loginUser = (AppUser) authenticationPrincipalArgumentResolver.resolveArgument(null, null, new ServletWebRequest(httpServletRequest), null);

        // then
        assertThat(loginUser.isGuest()).isFalse();
        assertThat(loginUser.getUsername()).isEqualTo(username);
        assertThat(loginUser.getAccessToken()).isEqualTo(accessToken);
    }

    @DisplayName("유효하지 않은 토큰이면 예외가 발생한다.")
    @Test
    void resolveArgument_InvalidToken_ThrowException() throws Exception {
        // given
        String jwtToken = "invalid jwt token";

        // mock
        when(httpServletRequest.getAttribute(AuthHeader.AUTHENTICATION)).thenReturn(jwtToken);

        // then
        assertThatThrownBy(() -> {
            authenticationPrincipalArgumentResolver.resolveArgument(null, null, new ServletWebRequest(httpServletRequest), null);
        }).isInstanceOf(InvalidTokenException.class);
    }

    @DisplayName("AccessToken DB에 저장되어 있지 않은 토큰이라면 예외가 발생한다.")
    @Test
    void resolveArgument_NotFoundToken_ThrowException() {
        // given
        String jwtToken = jwtTokenProvider.createToken("pick-git");

        // when
        when(httpServletRequest.getAttribute(AuthHeader.AUTHENTICATION)).thenReturn(jwtToken);

        assertThatThrownBy(() -> {
            authenticationPrincipalArgumentResolver.resolveArgument(null, null, new ServletWebRequest(httpServletRequest), null);
        }).isInstanceOf(InvalidTokenException.class);
    }

    @DisplayName("요청 헤더에 authorization을 추가해주지 않으면 Guest가 반환된다.")
    @Test
    void resolveArgument_InValidUserToken_ReturnGuest() throws Exception {
        // mock
        when(httpServletRequest.getAttribute(AuthHeader.AUTHENTICATION)).thenReturn(null);

        // when
        AppUser loginUser = (AppUser) authenticationPrincipalArgumentResolver.resolveArgument(null, null, new ServletWebRequest(httpServletRequest), null);

        // then
        assertThat(loginUser.isGuest()).isTrue();
        assertThatThrownBy(() -> loginUser.getAccessToken())
            .isInstanceOf(UnauthorizedException.class);
        assertThatThrownBy(() -> loginUser.getUsername())
            .isInstanceOf(UnauthorizedException.class);
    }
}
