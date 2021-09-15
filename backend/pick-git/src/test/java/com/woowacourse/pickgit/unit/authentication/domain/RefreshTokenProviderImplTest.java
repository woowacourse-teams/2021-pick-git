package com.woowacourse.pickgit.unit.authentication.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.woowacourse.pickgit.authentication.domain.JwtTokenProvider;
import com.woowacourse.pickgit.authentication.domain.RefreshTokenProvider;
import com.woowacourse.pickgit.authentication.infrastructure.JwtTokenProviderImpl;
import com.woowacourse.pickgit.authentication.infrastructure.RefreshTokenProviderImpl;
import com.woowacourse.pickgit.authentication.infrastructure.TokenBodyType;
import com.woowacourse.pickgit.exception.authentication.InvalidRefreshTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class RefreshTokenProviderImplTest {
    private static final String ACCESS_SECRET_KEY = "access_binghe819";
    private static final long ACCESS_EXPIRATIONTIMEINMILLISECONDS = 360000L;
    private static final String REFRESH_SECRET_KEY = "refresh_binghe819";
    private static final long REFRESH_EXPIRATIONTIMEINMILLISECONDS = 15768000000L;
    private static final String USERNAME = "binghe819";

    private JwtTokenProvider accessTokenProvider;
    private RefreshTokenProvider refreshTokenProvider;

    @BeforeEach
    void setUp() {
        accessTokenProvider = new JwtTokenProviderImpl(ACCESS_SECRET_KEY,
            ACCESS_EXPIRATIONTIMEINMILLISECONDS
        );
        refreshTokenProvider = new RefreshTokenProviderImpl(
            REFRESH_SECRET_KEY,
            REFRESH_EXPIRATIONTIMEINMILLISECONDS,
            accessTokenProvider
        );
    }

    @DisplayName("새로운 RefreshToken을 발급할 수 있다.")
    @Test
    void issue_newToken_Success() {
        // when
        String refreshToken = refreshTokenProvider.issueRefreshToken(USERNAME);

        // then
        assertThat(refreshToken).isNotBlank();
    }

    @DisplayName("새로운 RefreshToken의 payload값이 하나라도 비어 있으면 401 예외가 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void issue_payloadBlank_Fail(String username) {
        // when, then
        assertThatCode(() -> {
            refreshTokenProvider.issueRefreshToken(username);
        }).isInstanceOf(InvalidRefreshTokenException.class);
    }

    @DisplayName("유효한 RefreshToken으로 새로운 AccessToken을 발급할 수 있다.")
    @Test
    void reissue_newAccessToken_Success() {
        // given
        String refreshToken = refreshTokenProvider.issueRefreshToken(USERNAME);

        // when
        String reissuedAccessToken = refreshTokenProvider.reissueAccessToken(refreshToken);

        // then
        assertThat(accessTokenProvider.validateToken(reissuedAccessToken)).isTrue();
        assertThat(accessTokenProvider.getPayloadByKey(
            reissuedAccessToken,
            TokenBodyType.USERNAME.getValue()
        )).isEqualTo(USERNAME);
    }

    @DisplayName("유효하지 않는 RefreshToken으로 새로운 AccessToken을 발급하면 401 예외가 발생한다.")
    @Test
    void reissue_newAccessToken_Fail() {
        // given
        String refreshToken = "invalid refresh token";

        // when
        assertThatCode(() -> {
            refreshTokenProvider.reissueAccessToken(refreshToken);
        }).isInstanceOf(InvalidRefreshTokenException.class);
    }
}
