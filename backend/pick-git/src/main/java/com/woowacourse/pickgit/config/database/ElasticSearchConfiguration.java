package com.woowacourse.pickgit.config.database;

import com.woowacourse.pickgit.user.domain.search.UserSearchEngine;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@EnableElasticsearchRepositories(basePackageClasses = UserSearchEngine.class)
@Configuration
public class ElasticSearchConfiguration extends AbstractElasticsearchConfiguration {

    private final String elasticSearchHost;

    public ElasticSearchConfiguration(
        @Value("${security.elasticsearch.host}") String elasticSearchHost
    ) {
        this.elasticSearchHost = elasticSearchHost;
    }

    @Override
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
            .connectedTo(elasticSearchHost)
            .build();
        return RestClients.create(clientConfiguration).rest();
    }
}
