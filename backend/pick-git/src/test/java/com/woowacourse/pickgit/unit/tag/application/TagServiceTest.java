package com.woowacourse.pickgit.unit.tag.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.exception.post.TagFormatException;
import com.woowacourse.pickgit.tag.application.TagService;
import com.woowacourse.pickgit.tag.application.dto.ExtractionRequestDto;
import com.woowacourse.pickgit.tag.application.dto.TagsDto;
import com.woowacourse.pickgit.tag.domain.PlatformTagExtractor;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.tag.domain.TagRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @InjectMocks
    private TagService tagService;

    @Mock
    private PlatformTagExtractor platformTagExtractor;

    @Mock
    private TagRepository tagRepository;

    private final String accessToken = "abc";
    private final String userName = "asap";
    private final String repositoryName = "next-level";

    @DisplayName("Repository에 포함된 언어 태그를 추출한다.")
    @Test
    void extractTags_ValidRepository_ExtractionSuccess() {
        // given
        ExtractionRequestDto extractionRequestDto = ExtractionRequestDto
            .builder()
            .accessToken(accessToken)
            .userName(userName)
            .repositoryName(repositoryName)
            .build();
        List<String> tags = Arrays.asList("Java", "HTML", "CSS");

        // mock
        given(platformTagExtractor.extractTags(accessToken, userName, repositoryName))
            .willReturn(tags);

        // when
        TagsDto tagsDto = tagService.extractTags(extractionRequestDto);

        // then
        assertThat(tagsDto.getTagNames()).containsAll(Arrays.asList("java", "html", "css"));
        verify(platformTagExtractor, times(1)).extractTags(accessToken, userName, repositoryName);
    }

    @DisplayName("잘못된 경로로 태그 추출 요청시 예외가 발생한다.")
    @Test
    void extractTags_InvalidUrl_ExceptionThrown() {
        // given
        String userName = "nonuser";
        String repositoryName = "nonrepo";
        ExtractionRequestDto extractionRequestDto = ExtractionRequestDto
            .builder()
            .accessToken(accessToken)
            .userName(userName)
            .repositoryName(repositoryName)
            .build();

        // mock
        given(platformTagExtractor.extractTags(accessToken, userName, repositoryName))
            .willThrow(new PlatformHttpErrorException());

        // when, then
        assertThatCode(() -> tagService.extractTags(extractionRequestDto))
            .isInstanceOf(PlatformHttpErrorException.class)
            .extracting("errorCode")
            .isEqualTo("V0001");
        verify(platformTagExtractor, times(1))
            .extractTags(accessToken, userName, repositoryName);
    }

    @DisplayName("유효하지 않은 토큰으로 태그 추출 요청시 예외가 발생한다.")
    @Test
    void extractTags_InvalidToken_ExceptionThrown() {
        // given
        String accessToken = "invalidtoken";
        ExtractionRequestDto extractionRequestDto = ExtractionRequestDto
            .builder()
            .accessToken(accessToken)
            .userName(userName)
            .repositoryName(repositoryName)
            .build();

        // mock
        given(platformTagExtractor.extractTags(accessToken, userName, repositoryName))
            .willThrow(new PlatformHttpErrorException());

        // when, then
        assertThatCode(() -> tagService.extractTags(extractionRequestDto))
            .isInstanceOf(PlatformHttpErrorException.class)
            .extracting("errorCode")
            .isEqualTo("V0001");
        verify(platformTagExtractor, times(1))
            .extractTags(accessToken, userName, repositoryName);
    }

    @DisplayName("태그 이름을 태그로 변환한다.")
    @Test
    void findOrCreateTags_ValidTag_TransformationSuccess() {
        // given
        List<String> tagNames = Arrays.asList("Tag1", "tag2", "tag3");
        TagsDto tagsDto = new TagsDto(tagNames);

        // mock
        given(tagRepository.save(new Tag("tag1")))
            .willReturn(new Tag("tag1"));
        given(tagRepository.save(new Tag("tag2")))
            .willReturn(new Tag("tag2"));
        given(tagRepository.findByName("tag1"))
            .willReturn(Optional.empty());
        given(tagRepository.findByName("tag2"))
            .willReturn(Optional.empty());
        given(tagRepository.findByName("tag3"))
            .willReturn(Optional.of(new Tag("tag3")));

        // when
        List<String> tags = tagService.findOrCreateTags(tagsDto)
            .stream()
            .map(Tag::getName)
            .collect(Collectors.toList());

        // then
        assertThat(tags).containsAll(Arrays.asList("tag1", "tag2", "tag3"));
        verify(tagRepository, times(3)).findByName(anyString());
    }

    @DisplayName("잘못된 태그 이름을 태그로 변환 시도시 실패한다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "abcdeabcdeabcdeabcdea"})
    void findOrCreateTags_InvalidTagName_ExceptionThrown(String tagName) {
        // given
        List<String> tagNames = Arrays.asList("tag1", "tag2", tagName);
        TagsDto tagsDto = new TagsDto(tagNames);

        // mock
        given(tagRepository.findByName("tag1"))
            .willReturn(Optional.empty());
        given(tagRepository.findByName("tag2"))
            .willReturn(Optional.empty());

        // when, then
        assertThatCode(() -> tagService.findOrCreateTags(tagsDto))
            .isInstanceOf(TagFormatException.class)
            .extracting("errorCode")
            .isEqualTo("F0003");
        verify(tagRepository, times(2)).findByName(any());
    }

    @DisplayName("태그 이름이 없다면 빈 태그를 반환한다.")
    @Test
    void findOrCreateTags_NoneTagName_ReturnEmptyList() {
        // given
        TagsDto tagsDto = new TagsDto(null);

        // when
        List<Tag> tags = tagService.findOrCreateTags(tagsDto);

        // then
        assertThat(tags.size()).isZero();
    }
}
