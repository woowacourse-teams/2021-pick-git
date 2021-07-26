package com.woowacourse.pickgit.integration.tag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.exception.post.TagFormatException;
import com.woowacourse.pickgit.tag.application.ExtractionRequestDto;
import com.woowacourse.pickgit.tag.application.TagService;
import com.woowacourse.pickgit.tag.application.TagsDto;
import com.woowacourse.pickgit.tag.domain.PlatformTagExtractor;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.tag.domain.TagRepository;
import com.woowacourse.pickgit.tag.infrastructure.GithubTagExtractor;
import com.woowacourse.pickgit.common.mockapi.MockTagApiRequester;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class TagServiceIntegrationTest {

    private TagService tagService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TestEntityManager entityManager;

    private String accessToken = "oauth.access.token";
    private String userName = "jipark3";
    private String repositoryName = "doms-react";
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        PlatformTagExtractor platformTagExtractor =
            new GithubTagExtractor(new MockTagApiRequester(), objectMapper);
        tagService = new TagService(platformTagExtractor, tagRepository);
    }

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
        List<String> tags = Arrays.asList("JavaScript", "HTML", "CSS");

        // when
        TagsDto tagsDto = tagService.extractTags(extractionRequestDto);

        // then
        assertThat(tagsDto.getTagNames()).containsAll(Arrays.asList("javascript", "html", "css"));
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

        // when, then
        assertThatCode(() -> tagService.extractTags(extractionRequestDto))
            .isInstanceOf(PlatformHttpErrorException.class)
            .extracting("errorCode")
            .isEqualTo("V0001");
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

        // when, then
        assertThatCode(() -> tagService.extractTags(extractionRequestDto))
            .isInstanceOf(PlatformHttpErrorException.class)
            .extracting("errorCode")
            .isEqualTo("V0001");
    }

    @DisplayName("태그 이름을 태그로 변환한다.")
    @Test
    void findOrCreateTags_ValidTag_TransformationSuccess() {
        // given
        tagRepository.save(new Tag("tag3"));
        List<String> tagNames = Arrays.asList("Tag1", "tag2", "tag3");
        TagsDto tagsDto = new TagsDto(tagNames);

        entityManager.flush();
        entityManager.clear();

        // when
        List<String> tags = tagService.findOrCreateTags(tagsDto)
            .stream()
            .map(Tag::getName)
            .collect(Collectors.toList());

        // then
        assertThat(tags).containsAll(Arrays.asList("tag1", "tag2", "tag3"));
    }

    @DisplayName("잘못된 태그 이름을 태그로 변환 시도시 실패한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void findOrCreateTags_InvalidTagName_ExceptionThrown(String tagName) {
        // given
        List<String> tagNames = Arrays.asList("tag1", "tag2", tagName);
        TagsDto tagsDto = new TagsDto(tagNames);

        // when, then
        assertThatCode(() -> tagService.findOrCreateTags(tagsDto))
            .isInstanceOf(TagFormatException.class)
            .extracting("errorCode")
            .isEqualTo("F0003");
    }
}
