package com.woowacourse.pickgit.tag.application;

import com.woowacourse.pickgit.tag.domain.PlatformTagExtractor;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.tag.domain.TagRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TagService {

    private final PlatformTagExtractor platformTagExtractor;
    private final TagRepository tagRepository;

    public TagService(PlatformTagExtractor platformTagExtractor,
        TagRepository tagRepository) {
        this.platformTagExtractor = platformTagExtractor;
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    public TagsDto extractTags(ExtractionRequestDto extractionRequestDto) {
        String accessToken = extractionRequestDto.getAccessToken();
        String userName = extractionRequestDto.getUserName();
        String repositoryName = extractionRequestDto.getRepositoryName();
        List<String> tags = platformTagExtractor
            .extractTags(accessToken, userName, repositoryName);
        return new TagsDto(tags);
    }

    @Transactional(readOnly = true)
    public List<Tag> findOrCreateTags(TagsDto tagsDto) {
        if (Objects.isNull(tagsDto.getTagNames())) {
            return Collections.emptyList();
        }
        List<String> tagNames = tagsDto.getTagNames();
        List<Tag> tags = new ArrayList<>();
        for (String tagName : tagNames) {
            tagRepository.findByName(tagName)
                .ifPresentOrElse(tags::add,
                    () -> tags.add(tagRepository.save(new Tag(tagName)))
                );
        }
        return tags;
    }
}
