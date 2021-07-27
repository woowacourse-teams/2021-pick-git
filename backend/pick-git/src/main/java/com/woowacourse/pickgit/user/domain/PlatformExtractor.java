package com.woowacourse.pickgit.user.domain;

import com.woowacourse.pickgit.user.domain.dto.CountResponseDto;
import com.woowacourse.pickgit.user.domain.dto.StarResponseDto;
import java.util.List;

public interface PlatformExtractor {

    CountResponseDto extractCount(String restUrl, String username);

    List<StarResponseDto> extractStars(String username);
}