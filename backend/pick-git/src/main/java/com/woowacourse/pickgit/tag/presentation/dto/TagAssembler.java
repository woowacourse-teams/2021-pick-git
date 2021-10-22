package com.woowacourse.pickgit.tag.presentation.dto;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.tag.application.dto.ExtractionRequestDto;

public class TagAssembler {

    public static ExtractionRequestDto extractionRequestDto(
        AppUser appUser,
        String repositoryName
    ) {
        return ExtractionRequestDto.builder()
            .accessToken(appUser.getAccessToken())
            .userName(appUser.getUsername())
            .repositoryName(repositoryName)
            .build();
    }

}
