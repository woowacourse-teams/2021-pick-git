package com.woowacourse.pickgit.tag.presentation;

import com.woowacourse.pickgit.tag.application.ExtractionRequestDto;
import com.woowacourse.pickgit.tag.application.TagService;
import com.woowacourse.pickgit.tag.application.TagsDto;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/github/{userName}/repositories/{repositoryName}/tags/languages")
    public ResponseEntity<TagsDto> extractLanguageTags(HttpServletRequest httpServletRequest,
        @PathVariable String userName,
        @PathVariable String repositoryName) {
        String token = httpServletRequest.getHeader("Authorization")
            .split(" ")[1];
        ExtractionRequestDto extractionRequestDto =
            new ExtractionRequestDto(token, userName, repositoryName);
        TagsDto tagsDto = tagService.extractTags(extractionRequestDto);
        return ResponseEntity.ok(tagsDto);
    }
}
