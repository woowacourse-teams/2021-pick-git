package com.woowacourse.pickgit.tag.application;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.tag.application.dto.ExtractionRequestDto;
import com.woowacourse.pickgit.tag.application.dto.TagsDto;
import com.woowacourse.pickgit.tag.domain.PlatformTagExtractor;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.tag.domain.TagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TagService {

    private final PlatformTagExtractor platformTagExtractor;
    private final TagRepository tagRepository;

    @Transactional(propagation = Propagation.NEVER)
    public TagsDto extractTags(ExtractionRequestDto extractionRequestDto) {
        String accessToken = extractionRequestDto.getAccessToken();
        String userName = extractionRequestDto.getUserName();
        String repositoryName = extractionRequestDto.getRepositoryName();

        List<String> tags = platformTagExtractor
            .extractTags(accessToken, userName, repositoryName)
            .stream()
            .map(String::toLowerCase)
            .collect(toList());
        return new TagsDto(tags);
    }

    public List<Tag> findOrCreateTags(TagsDto tagsDto) {
        List<String> tagNames = tagsDto.getTagNames();

        return tagNames.stream()
            .map(this::getTagOrCreateTagIfNotExist)
            .collect(toList());
    }

    private Tag getTagOrCreateTagIfNotExist(String tagName) {
        Tag tag = new Tag(tagName);
        return tagRepository.findByName(tag.getName())
            .orElseGet(() -> tagRepository.save(tag));
    }
}
