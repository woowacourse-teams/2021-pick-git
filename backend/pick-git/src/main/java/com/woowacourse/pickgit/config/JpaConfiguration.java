package com.woowacourse.pickgit.config;

import com.woowacourse.pickgit.comment.domain.CommentRepository;
import com.woowacourse.pickgit.portfolio.domain.repository.PortfolioRepository;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.tag.domain.TagRepository;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@EnableJpaRepositories(excludeFilters = @ComponentScan.Filter(
//    type = FilterType.ASSIGNABLE_TYPE,
//    classes = UserSearchEngine.class))
@EnableJpaRepositories(basePackageClasses = {UserRepository.class, PostRepository.class,
    CommentRepository.class, TagRepository.class, PortfolioRepository.class})
@EnableJpaAuditing
@Configuration
public class JpaConfiguration {
}
