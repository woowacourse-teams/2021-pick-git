package com.woowacourse.pickgit.unit.authentication.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.authentication.domain.OAuthAccessTokenDao;
import com.woowacourse.pickgit.authentication.infrastructure.dao.CollectionOAuthAccessTokenDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OAuthAccessTokenDaoTest {

    private static final String TOKEN = "jwt token";
    private static final String OAUTH_ACCESS_TOKEN = "oauth access token";

    private OAuthAccessTokenDao oAuthAccessTokenDao;

    @BeforeEach
    void setUp() {
        // given
        oAuthAccessTokenDao = new CollectionOAuthAccessTokenDao();
    }

    @DisplayName("처음 생성된 JWT 토큰과 OAuth Access Token을 맵에 성공적으로 저장하고 불러온다.")
    @Test
    void insertAndFind_NonDuplicated_Save() {
        // when
        oAuthAccessTokenDao.insert(TOKEN, OAUTH_ACCESS_TOKEN);

        // then
        assertThat(oAuthAccessTokenDao.findByKeyToken(TOKEN).get()).isEqualTo(OAUTH_ACCESS_TOKEN);
    }

    @DisplayName("이미 있는 JWT 토큰에 대해서 새로운 OAuth Access Token을 추가해도 성공적으로 덮어씌워진다.")
    @Test
    void insertAndFind_Duplicated_Save() {
        // when
        oAuthAccessTokenDao.insert(TOKEN, OAUTH_ACCESS_TOKEN);
        oAuthAccessTokenDao.insert(TOKEN, "duplicated");

        // then
        assertThat(oAuthAccessTokenDao.findByKeyToken(TOKEN).get()).isEqualTo("duplicated");
    }
}
