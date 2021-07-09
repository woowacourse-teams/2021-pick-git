package com.woowacourse.pickgit.tag.application;

import com.woowacourse.pickgit.tag.domain.PlatformTagExtractor;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TagService {

    private final PlatformTagExtractor platformTagExtractor;

    public TagService(PlatformTagExtractor platformTagExtractor) {
        this.platformTagExtractor = platformTagExtractor;
    }

    public TagsDto extractTags(ExtractionRequestDto extractionRequestDto) {
        String accessToken = extractionRequestDto.getAccessToken();
        String userName = extractionRequestDto.getUserName();
        String repositoryName = extractionRequestDto.getRepositoryName();
        List<String> tags = platformTagExtractor
            .extractTags(accessToken, userName, repositoryName);
        return new TagsDto(tags);
    }
}
