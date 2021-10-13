package com.woowacourse.pickgit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Profile("test")
@Component
public class SearchEngineCleaner {

    @Value("${security.elasticsearch.host}")
    private String elasticSearchHost;

    public void clearUsers() {
        try {
            new RestTemplate().delete("http://" + elasticSearchHost + "/users");
        } catch (HttpClientErrorException ignored) {
        }
    }
}
