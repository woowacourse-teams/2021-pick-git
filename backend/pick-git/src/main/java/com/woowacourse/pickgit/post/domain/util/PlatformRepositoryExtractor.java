package com.woowacourse.pickgit.post.domain.util;

import com.woowacourse.pickgit.post.domain.util.dto.RepositoryUrlAndName;
import java.util.List;

public interface PlatformRepositoryExtractor {

    List<RepositoryUrlAndName> extract(String token, String username);
}
