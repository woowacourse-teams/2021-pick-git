package com.woowacourse.pickgit.post.domain;

import com.woowacourse.pickgit.post.infrastructure.dto.RepositoryResponse;
import java.util.List;

public interface PlatformRepositoryExtractor {

    List<RepositoryResponse> extract(String token);
}
