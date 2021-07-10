package com.woowacourse.pickgit.authentication.application;

import static org.junit.jupiter.api.Assertions.*;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

class OAuthServiceTest {

    @Test
    void github() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Authorization", "Bearer " + "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");

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(headers);

        RestTemplate restTemplate = new RestTemplate();
        OAuthProfileResponse result = restTemplate
            .exchange("https://api.github.com/user", HttpMethod.GET, httpEntity, OAuthProfileResponse.class)
            .getBody();
        System.out.println(result.toString());
    }
}
