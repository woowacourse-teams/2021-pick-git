package com.woowacourse.pickgit.tag.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.woowacourse.pickgit.tag.domain.PlatformTagExtractor;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @InjectMocks
    private TagService tagService;

    @Mock
    private PlatformTagExtractor platformTagExtractor;

    @DisplayName("Repository에 포함된 언어 태그를 추출한다.")
    @Test
    void extractTags_ValidRepository_ExtractionSuccess() {
        String accessToken = "abc";
        String userName = "asap";
        String repositoryName = "next-level";
        ExtractionRequestDto extractionRequestDto = new ExtractionRequestDto(accessToken, userName,
            repositoryName);
        List<String> tags = Arrays.asList("Java", "HTML", "CSS");

        given(platformTagExtractor.extractTags(accessToken, userName, repositoryName))
            .willReturn(tags);

        TagsDto tagsDto = tagService.extractTags(extractionRequestDto);

        assertThat(tagsDto.getTags()).containsAll(tags);
        verify(platformTagExtractor, times(1)).extractTags(accessToken, userName, repositoryName);
    }
}
