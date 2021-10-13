package com.woowacourse.pickgit.config;

import com.woowacourse.pickgit.comment.domain.CommentRepository;
import com.woowacourse.pickgit.portfolio.domain.repository.PortfolioRepository;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.tag.domain.TagRepository;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import com.woowacourse.pickgit.user.domain.search.UserSearchEngine;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableElasticsearchRepositories(basePackageClasses = UserSearchEngine.class)
@EnableJpaRepositories(basePackageClasses = {UserRepository.class, PostRepository.class,
    CommentRepository.class, TagRepository.class, PortfolioRepository.class})
@EnableJpaAuditing
@TestConfiguration
public class JpaTestConfiguration extends AbstractElasticsearchConfiguration {

    @Value("${security.elasticsearch.host}")
    private String elasticSearchHost;

    @Override
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
            .connectedTo(elasticSearchHost)
            .build();
        return RestClients.create(clientConfiguration).rest();
    }
}
