package com.woowacourse.pickgit.post.domain;

import com.woowacourse.pickgit.post.domain.dto.RepositoryResponseDto;
import java.util.List;

public interface PlatformRepositoryExtractor {

    List<RepositoryResponseDto> extract(String token, String username);
}
