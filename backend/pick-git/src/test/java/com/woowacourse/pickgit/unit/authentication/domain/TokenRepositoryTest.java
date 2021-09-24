package com.woowacourse.pickgit.unit.authentication.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.authentication.domain.Token;
import com.woowacourse.pickgit.authentication.domain.TokenRepository;
import com.woowacourse.pickgit.common.mockapi.MockTokenRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenRepositoryTest {

    private TokenRepository tokenRepository;

    @BeforeEach
    void setUp() {
        tokenRepository = new MockTokenRepository();
    }

    @Test
    void save() {
        // given
        Token token = new Token("key", "refresh token", "oauth token");

        // when
        Token savedToken = tokenRepository.save(token);

        // then
        assertThat(token.getRefreshToken()).isEqualTo(savedToken.getRefreshToken());
        assertThat(token.getOauthToken()).isEqualTo(savedToken.getOauthToken());
    }

    @Test
    void findById() {
        // given
        Token savedToken = tokenRepository.save(
            new Token("key", "refresh token", "oauth token")
        );

        // when
        Optional<Token> findToken = tokenRepository.findById(savedToken.getId());

        // then
        assertThat(findToken).isPresent();
        assertThat(findToken).contains(savedToken);
    }

    @Test
    void delete() {
        // given
        Token savedToken = tokenRepository.save(
            new Token("key", "refresh token", "oauth token")
        );

        // when
        tokenRepository.deleteById(savedToken.getId());

        // then
        assertThat(tokenRepository.findById(savedToken.getId()))
            .isNotPresent();
    }
}
