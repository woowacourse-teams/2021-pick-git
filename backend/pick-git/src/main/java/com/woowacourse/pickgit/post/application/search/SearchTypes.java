package com.woowacourse.pickgit.post.application.search;

import com.woowacourse.pickgit.exception.post.IllegalSearchTypeException;
import com.woowacourse.pickgit.post.application.search.type.SearchType;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SearchTypes {

    private final List<SearchType> searchTypes;

    public SearchTypes(List<SearchType> searchTypes) {
        this.searchTypes = searchTypes;
    }

    public SearchType findByTypeName(String typeName) {
        return searchTypes.stream()
            .filter(searchType -> searchType.isSatisfiedBy(typeName))
            .findAny()
            .orElseThrow(IllegalSearchTypeException::new);
    }
}
