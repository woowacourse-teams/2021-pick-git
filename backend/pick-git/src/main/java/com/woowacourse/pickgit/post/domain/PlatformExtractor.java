package com.woowacourse.pickgit.post.domain;

import com.woowacourse.pickgit.post.infrastructure.dto.RepositoryResponse;
import java.util.List;

public interface PlatformExtractor {

    List<RepositoryResponse> showRepositories(String token);
}
