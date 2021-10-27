package com.woowacourse.pickgit.user.domain.search;

import com.woowacourse.pickgit.user.domain.User;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;

@RequiredArgsConstructor
public class CustomUserSearchEngineImpl implements CustomUserSearchEngine {

    private final ElasticsearchOperations elasticsearchOperations;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<User> searchByUsernameLike(String username, Pageable pageable) {
        Criteria criteria = Criteria.where("basicProfile.name").contains(username);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        SearchHits<User> search = elasticsearchOperations.search(query, User.class);
        List<Long> ids = search.stream()
            .map(SearchHit::getContent)
            .map(User::getId)
            .collect(Collectors.toList());
        return findAllByIds(ids);
    }

    private List<User> findAllByIds(List<Long> ids) {
        String query = "select u from User u where u.id in (:ids)";
        return entityManager.createQuery(query, User.class)
            .setParameter("ids", ids)
            .getResultList();
    }
}
