package com.woowacourse.pickgit.authentication.application;

import static org.junit.jupiter.api.Assertions.*;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class OAuthServiceTest {

    @DisplayName("처음 사이트에 로그인 한 경우 유저 정보를 Github으로부터 받아와서 DB에 저장한다.")
    @Test
    void test1() {
    }

    @DisplayName("처음 사이트에 로그인이 아닌 경우 유저 정보를 Github로부터 받아와서 DB 정보를 최신화한다.")
    @Test
    void test2() {

    }

    @DisplayName("처음 ")
    @Test
    void test3() {

    }
}
