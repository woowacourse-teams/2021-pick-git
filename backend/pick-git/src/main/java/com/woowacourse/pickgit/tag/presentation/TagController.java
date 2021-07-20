package com.woowacourse.pickgit.tag.presentation;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.tag.application.ExtractionRequestDto;
import com.woowacourse.pickgit.tag.application.TagService;
import com.woowacourse.pickgit.tag.application.TagsDto;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(value = "*")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/github/repositories/{repositoryName}/tags/languages")
    public ResponseEntity<List<String>> extractLanguageTags(@Authenticated AppUser appUser,
        @PathVariable String repositoryName) {
        String accessToken = appUser.getAccessToken();
        ExtractionRequestDto extractionRequestDto =
            new ExtractionRequestDto(accessToken, appUser.getUsername(), repositoryName);
        TagsDto tagsDto = tagService.extractTags(extractionRequestDto);
        return ResponseEntity.ok(tagsDto.getTags());
    }
}
