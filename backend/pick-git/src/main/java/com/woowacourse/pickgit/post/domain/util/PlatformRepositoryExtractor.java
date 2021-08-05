package com.woowacourse.pickgit.post.domain.util;

import com.woowacourse.pickgit.post.domain.util.dto.RepositoryNameAndUrl;
import java.util.List;

public interface PlatformRepositoryExtractor {

    List<RepositoryNameAndUrl> extract(String token, String username, Long page, Long limit);
}
