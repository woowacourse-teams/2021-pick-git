package com.woowacourse.pickgit.tag.presentation;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.config.auth_interceptor_register.ForOnlyLoginUser;
import com.woowacourse.pickgit.tag.application.TagService;
import com.woowacourse.pickgit.tag.application.dto.ExtractionRequestDto;
import com.woowacourse.pickgit.tag.application.dto.TagsDto;
import com.woowacourse.pickgit.tag.presentation.dto.TagAssembler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@CrossOrigin(value = "*")
@RequestMapping("/api")
@RestController
public class TagController {

    private final TagService tagService;

    @ForOnlyLoginUser
    @GetMapping("/github/repositories/{repositoryName}/tags/languages")
    public ResponseEntity<List<String>> extractLanguageTags(
        @Authenticated AppUser appUser,
        @PathVariable String repositoryName
    ) {
        ExtractionRequestDto extractionRequestDto =
            TagAssembler.extractionRequestDto(appUser,repositoryName);

        TagsDto tagsDto = tagService.extractTags(extractionRequestDto);

        return ResponseEntity.ok(tagsDto.getTagNames());
    }
}
