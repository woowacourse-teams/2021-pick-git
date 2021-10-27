package com.woowacourse.pickgit.post.application.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.exception.post.IllegalSearchTypeException;
import com.woowacourse.pickgit.post.application.search.type.SearchType;
import com.woowacourse.pickgit.post.domain.Post;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.data.domain.Pageable;

class SearchTypesTest {

    private Post post;

    @BeforeEach
    void setUp() {
        post = Post.builder()
            .id(1L)
            .build();
    }

    @DisplayName("맞는 타입이 있으면 SearchType을 반환한다.")
    @ParameterizedTest
    @CsvSource(value = {"A", "B"})
    void findByTypeName_ValidType_ReturnSearchType(String type) {
        SearchTypes searchTypes = new SearchTypes(
            List.of(
                createSearchType("A"),
                createSearchType("B")
            )
        );

        assertThat(searchTypes.findByTypeName(type)).isNotNull();
    }

    @DisplayName("맞는 타입이 없으면 예외를 발생한다.")
    @Test
    void findByTypeName_InvalidType_ExceptionOccur() {
        SearchTypes searchTypes = new SearchTypes(
            List.of(
                createSearchType("A"),
                createSearchType("B")
            )
        );

        assertThatThrownBy(() -> searchTypes.findByTypeName("C"))
            .isInstanceOf(IllegalSearchTypeException.class)
            .extracting("errorCode")
            .isEqualTo("P0006");
    }

    private SearchType createSearchType(String TYPE) {
        return new SearchType() {
            @Override
            public boolean isSatisfiedBy(String searchType) {
                return TYPE.equals(searchType);
            }

            @Override
            public List<Post> search(String keywords, Pageable pageRequest) {
                return List.of(post);
            }
        };
    }
}
