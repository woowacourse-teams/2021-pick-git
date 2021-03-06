package com.woowacourse.pickgit.integration.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.woowacourse.pickgit.authentication.application.JwtTokenProvider;
import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.domain.OAuthAccessTokenDao;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.infrastructure.JwtTokenProviderImpl;
import com.woowacourse.pickgit.authentication.infrastructure.dao.CollectionOAuthAccessTokenDao;
import com.woowacourse.pickgit.authentication.presentation.AuthenticationPrincipalArgumentResolver;
import com.woowacourse.pickgit.authentication.presentation.interceptor.AuthHeader;
import com.woowacourse.pickgit.exception.authentication.InvalidTokenException;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.ServletWebRequest;

@ExtendWith(MockitoExtension.class)
class AuthenticationPrincipalArgumentResolverIntegrationTest {

    private JwtTokenProvider jwtTokenProvider;

    private OAuthAccessTokenDao oAuthAccessTokenDao;

    @Mock
    private HttpServletRequest httpServletRequest;

    private OAuthService oAuthService;

    private AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver;

    @BeforeEach
    void setUp() {
        jwtTokenProvider =
            new JwtTokenProviderImpl("pick-git", 3600000);
        oAuthAccessTokenDao =
            new CollectionOAuthAccessTokenDao();
        oAuthService =
            new OAuthService(null, jwtTokenProvider, oAuthAccessTokenDao, null, null);
        authenticationPrincipalArgumentResolver =
            new AuthenticationPrincipalArgumentResolver(oAuthService);
    }

    @DisplayName("????????? ???????????? LoginUser??? ????????????.")
    @Test
    void resolveArgument_ValidUserToken_ReturnLoginUser() throws Exception {
        // given
        String username = "pick-git";
        String accessToken = "oauth access token";
        String jwtToken = jwtTokenProvider.createToken(username);

        oAuthAccessTokenDao.insert(jwtToken, accessToken);

        // mock
        given(httpServletRequest.getAttribute(AuthHeader.AUTHENTICATION)).willReturn(jwtToken);

        // when
        AppUser loginUser = (AppUser) authenticationPrincipalArgumentResolver
            .resolveArgument(null, null, new ServletWebRequest(httpServletRequest), null);

        // then
        assertThat(loginUser.isGuest()).isFalse();
        assertThat(loginUser.getUsername()).isEqualTo(username);
        assertThat(loginUser.getAccessToken()).isEqualTo(accessToken);
    }

    @DisplayName("???????????? ?????? ???????????? ????????? ????????????.")
    @Test
    void resolveArgument_InvalidToken_ThrowException() throws Exception {
        // given
        String jwtToken = "invalid jwt token";

        // mock
        given(httpServletRequest.getAttribute(AuthHeader.AUTHENTICATION)).willReturn(jwtToken);

        // then
        assertThatThrownBy(() -> {
            authenticationPrincipalArgumentResolver
                .resolveArgument(null, null, new ServletWebRequest(httpServletRequest), null);
        }).isInstanceOf(InvalidTokenException.class);
    }

    @DisplayName("AccessToken DB??? ???????????? ?????? ?????? ??????????????? ????????? ????????????.")
    @Test
    void resolveArgument_NotFoundToken_ThrowException() {
        // given
        String jwtToken = jwtTokenProvider.createToken("pick-git");

        // when
        given(httpServletRequest.getAttribute(AuthHeader.AUTHENTICATION)).willReturn(jwtToken);

        assertThatThrownBy(() -> {
            authenticationPrincipalArgumentResolver
                .resolveArgument(null, null, new ServletWebRequest(httpServletRequest), null);
        }).isInstanceOf(InvalidTokenException.class);
    }

    @DisplayName("?????? ????????? authorization??? ??????????????? ????????? Guest??? ????????????.")
    @Test
    void resolveArgument_InValidUserToken_ReturnGuest() throws Exception {
        // mock
        given(httpServletRequest.getAttribute(AuthHeader.AUTHENTICATION)).willReturn(null);

        // when
        AppUser loginUser = (AppUser) authenticationPrincipalArgumentResolver
            .resolveArgument(null, null, new ServletWebRequest(httpServletRequest), null);

        // then
        assertThat(loginUser.isGuest()).isTrue();
        assertThatThrownBy(() -> loginUser.getAccessToken())
            .isInstanceOf(UnauthorizedException.class);
        assertThatThrownBy(() -> loginUser.getUsername())
            .isInstanceOf(UnauthorizedException.class);
    }
}
