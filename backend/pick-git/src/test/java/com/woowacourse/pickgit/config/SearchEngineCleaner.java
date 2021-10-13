package com.woowacourse.pickgit.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Profile("test")
@Component
public class SearchEngineCleaner implements InitializingBean {

    @Value("${security.elasticsearch.host}")
    private String elasticSearchHost;

    @Override
    public void afterPropertiesSet() throws Exception {
        clearUsers();
    }

    public void clearUsers() {
        String query = "{\n"
            + "  \"query\": {\n"
            + "    \"range\" : {\n"
            + "        \"id\" : {\n"
            + "           \"gte\" : 1\n"
            + "        }\n"
            + "    }\n"
            + "  }\n"
            + "}";
        String url = "http://" + elasticSearchHost + "/users/_delete_by_query";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> httpEntity = new HttpEntity<>(query, headers);

        try {
            new RestTemplate().exchange(url, HttpMethod.POST, httpEntity, String.class);
        } catch (HttpClientErrorException ignored) {
            ignored.printStackTrace();
        }
    }
}
