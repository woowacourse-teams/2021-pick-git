package com.woowacourse.pickgit.authentication.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
class OAuthAccessTokenDaoTest {

    @Autowired
    private OAuthAccessTokenDao oAuthAccessTokenDao;

    private String token;
    private String oauthAccessToken;

    @BeforeEach
    void setUp() {
        // given
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
