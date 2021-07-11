package com.woowacourse.pickgit.tag.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @InjectMocks
    private TagService tagService;

    @Mock
    private PlatformTagExtractor platformTagExtractor;

    private String accessToken = "abc";
    private String userName = "asap";
    private String repositoryName = "next-level";

    @DisplayName("Repository에 포함된 언어 태그를 추출한다.")
    @Test
    void extractTags_ValidRepository_ExtractionSuccess() {
        ExtractionRequestDto extractionRequestDto =
            new ExtractionRequestDto(accessToken, userName, repositoryName);
        List<String> tags = Arrays.asList("Java", "HTML", "CSS");

        given(platformTagExtractor.extractTags(accessToken, userName, repositoryName))
            .willReturn(tags);

        TagsDto tagsDto = tagService.extractTags(extractionRequestDto);

        assertThat(tagsDto.getTags()).containsAll(tags);
        verify(platformTagExtractor, times(1)).extractTags(accessToken, userName, repositoryName);
    }

    @DisplayName("잘못된 경로로 태그 추출 요청시 404 예외가 발생한다.")
    @Test
    void extractTags_InvalidUrl_ExceptionThrown() {
        String userName = "nonuser";
        String repositoryName = "nonrepo";
        ExtractionRequestDto extractionRequestDto =
            new ExtractionRequestDto(accessToken, userName, repositoryName);

        given(platformTagExtractor.extractTags(accessToken, userName, repositoryName))
            .willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThatCode(() -> tagService.extractTags(extractionRequestDto))
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessageContaining("404");
        verify(platformTagExtractor, times(1))
            .extractTags(accessToken, userName, repositoryName);
    }

    @DisplayName("유효하지 않은 토큰으로 태그 추출 요청시 401 예외가 발생한다.")
    @Test
    void extractTags_InvalidToken_ExceptionThrown() {
        String accessToken = "invalidtoken";
        ExtractionRequestDto extractionRequestDto =
            new ExtractionRequestDto(accessToken, userName, repositoryName);

        given(platformTagExtractor.extractTags(accessToken, userName, repositoryName))
            .willThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        assertThatCode(() -> tagService.extractTags(extractionRequestDto))
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessageContaining("401");
        verify(platformTagExtractor, times(1))
            .extractTags(accessToken, userName, repositoryName);
    }
}
