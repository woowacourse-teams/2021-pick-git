package com.woowacourse.pickgit.unit.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

import com.woowacourse.pickgit.post.application.search.type.TagType;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class TagTypeTest {

    @InjectMocks
    private TagType tagType;

    @Mock
    private PostRepository postRepository;

    @DisplayName("검색 타입이 유효한지 확인한다.")
    @ParameterizedTest
    @CsvSource(value = {"tags, true", "name, false", "test, false"})
    void isSatisfiedBy_checkSearchType_Success(String type, boolean expected) {
        assertThat(tagType.isSatisfiedBy(type)).isEqualTo(expected);
    }

    @DisplayName("keyword를 기준으로 검색한다.")
    @Test
    void search_findByKeyword_Success() {
        Post post = Post.builder()
            .id(1L)
            .build();

        given(postRepository.findAllPostsByTagNames(anyList(), any(Pageable.class)))
            .willReturn(List.of(post));

        PageRequest pageRequest = PageRequest.of(0, 1);
        List<Post> actual = tagType.search("keyword", pageRequest);

        assertThat(actual.size()).isEqualTo(1);
    }
}
