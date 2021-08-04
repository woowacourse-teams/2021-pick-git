package com.woowacourse.pickgit.user.domain;

import com.woowacourse.pickgit.user.infrastructure.dto.CountDto;
import com.woowacourse.pickgit.user.infrastructure.dto.ItemDto;
import com.woowacourse.pickgit.user.infrastructure.dto.StarsDto;
import java.util.List;

public interface PlatformContributionExtractor {

    ItemDto extractStars(String accessToken, String username);

    CountDto extractCount(String restUrl, String accessToken, String username);
}
