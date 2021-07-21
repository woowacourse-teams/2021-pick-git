package com.woowacourse.pickgit.unit.authentication.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.authentication.dao.CollectionOAuthAccessTokenDao;
import com.woowacourse.pickgit.authentication.dao.OAuthAccessTokenDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OAuthAccessTokenDaoTest {

    private OAuthAccessTokenDao oAuthAccessTokenDao;

    private String token;
    private String oauthAccessToken;

    @BeforeEach
    void setUp() {
        // given
        oAuthAccessTokenDao = new CollectionOAuthAccessTokenDao();
        token = "jwt token";
        oauthAccessToken = "oauth access token";
    }

    @Test
    void insertAndFind_NonDuplicated_Save() {
        // when
        oAuthAccessTokenDao.insert(token, oauthAccessToken);

        // then
        assertThat(oAuthAccessTokenDao.findByKeyToken(token).get()).isEqualTo(oauthAccessToken);
    }

    @Test
    void insertAndFind_Duplicated_Save() {
        // when
        oAuthAccessTokenDao.insert(token, oauthAccessToken);

        oAuthAccessTokenDao.insert(token, "duplicated");

        // then
        assertThat(oAuthAccessTokenDao.findByKeyToken(token).get()).isEqualTo("duplicated");
    }
}
