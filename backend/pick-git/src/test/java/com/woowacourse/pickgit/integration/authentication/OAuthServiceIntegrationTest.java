package com.woowacourse.pickgit.integration.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.authentication.application.JwtTokenProvider;
import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.application.dto.TokenDto;
import com.woowacourse.pickgit.authentication.domain.OAuthAccessTokenDao;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.authentication.infrastructure.JwtTokenProviderImpl;
import com.woowacourse.pickgit.authentication.infrastructure.dao.RedisOAuthAccessTokenDao;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import com.woowacourse.pickgit.exception.authentication.InvalidTokenException;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.integration.IntegrationTest;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import com.woowacourse.pickgit.user.domain.search.UserSearchEngine;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Import(InfrastructureTestConfiguration.class)
@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
class OAuthServiceIntegrationTest extends IntegrationTest {

    private static final String SECRET_KEY = "secret-key";
    private static final Long EXPIRATION_TIME_IN_MILLISECONDS = 3600000L;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSearchEngine userSearchEngine;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private OAuthClient oAuthClient;

    private JwtTokenProvider jwtTokenProvider;

    private OAuthAccessTokenDao oAuthAccessTokenDao;

    private OAuthService oAuthService;

    @BeforeEach
    void setUp() {
        this.jwtTokenProvider = new JwtTokenProviderImpl(
            SECRET_KEY,
            EXPIRATION_TIME_IN_MILLISECONDS
        );
        this.oAuthAccessTokenDao = new RedisOAuthAccessTokenDao(
            redisTemplate,
            Duration.ofHours(3).toMillis()
        );
        this.oAuthService = new OAuthService(
            oAuthClient,
            jwtTokenProvider,
            oAuthAccessTokenDao,
            userRepository,
            userSearchEngine
        );
    }

    @DisplayName("Github 로그인 URL을 반환한다.")
    @Test
    void getGithubAuthorizationUrl_Anonymous_ReturnGithubAuthorizationUrl() {
        // mock

        // when
        String githubAuthorizationUrl =
            oAuthService.getGithubAuthorizationUrl();

        // then
        assertThat(githubAuthorizationUrl)
            .startsWith("https://github.com/login/oauth/authorize?");
    }

    @DisplayName("회원가입(첫 로그인)시 Github Profile을 가져와서 DB에 insert한다.")
    @Test
    void createToken_Signup_SaveUserProfile() {
        // given
        String code = "binghe";
        String oauthAccessToken = "oauth access token";
        OAuthProfileResponse oAuthProfileResponse = OAuthProfileResponse.builder()
            .name("binghe")
            .image("image")
            .githubUrl("github.com/")
            .build();

        // when
        TokenDto token = oAuthService.createToken(code);

        // then
        assertThat(token.getToken()).isNotNull();
        assertThat(token.getUsername()).isEqualTo(oAuthProfileResponse.getName());

        User user = userRepository.findByBasicProfile_Name(token.getUsername()).orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("binghe");
        assertThat(user.getImage()).isEqualTo("https://github.com/testImage.jpg");
        assertThat(user.getGithubUrl()).isEqualTo("https://github.com/binghe");
    }

    @DisplayName("로그인(첫 로그인이 아닌경우)시 Github Profile을 가져와서 DB에 저장된 기존 정보를 update한다.")
    @Test
    void createToken_Signup_UpdateUserProfile() {
        // given
        String code = "binghe";
        String oauthAccessToken = "oauth access token";
        OAuthProfileResponse previousOAuthProfileResponse = OAuthProfileResponse.builder()
            .name("binghe")
            .image("image")
            .githubUrl("github.com/")
            .build();

        User existingUser = new User(previousOAuthProfileResponse.toBasicProfile(),
            previousOAuthProfileResponse.toGithubProfile());
        userRepository.save(existingUser);

        OAuthProfileResponse changedOAuthProfileResponse = OAuthProfileResponse.builder()
            .name("binghe")
            .image("image")
            .githubUrl("github.com/")
            .company("@woowabros")
            .build();

        // when
        TokenDto token = oAuthService.createToken(code);

        // then
        assertThat(token.getToken()).isNotNull();
        assertThat(token.getUsername()).isEqualTo(changedOAuthProfileResponse.getName());

        User user = userRepository.findByBasicProfile_Name(token.getUsername()).orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("binghe");
        assertThat(user.getImage()).isEqualTo("image");
        assertThat(user.getGithubUrl()).isEqualTo("https://github.com/binghe");
        assertThat(user.getCompany()).isEqualTo("testCompany");
    }

    @DisplayName("JWT 토큰을 통해 AccessTokenDB에서 LoginUser에 대한 정보를 가져온다.")
    @Test
    void findRequestUserByToken_ValidToken_ReturnAppUser() {
        // given
        String username = "pick-git";
        String token = jwtTokenProvider.createToken(username);
        String accessToken = "oauth access token";

        oAuthAccessTokenDao.insert(token, accessToken);

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
        String username = "pick-git-test";
        String token = jwtTokenProvider.createToken(username);

        // when, then
        assertThatThrownBy(() -> oAuthService.findRequestUserByToken(token))
            .isInstanceOf(InvalidTokenException.class);
    }

    @DisplayName("authentication이 Null 이라면 GuestUser를 반환한다.")
    @Test
    void findRequestUserByToken_NullAuthenticationParam_ReturnGuestUser() {
        // given
        // when
        AppUser appUser = oAuthService.findRequestUserByToken(null);

        // then
        assertThat(appUser).isInstanceOf(GuestUser.class);
        assertThatThrownBy(appUser::getUsername).isInstanceOf(UnauthorizedException.class);
        assertThatThrownBy(appUser::getAccessToken).isInstanceOf(UnauthorizedException.class);
    }
}
