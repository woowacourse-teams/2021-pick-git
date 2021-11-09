package com.woowacourse.pickgit.post.domain.util;

import com.woowacourse.pickgit.post.domain.util.dto.RepositoryNameAndUrl;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface PlatformRepositorySearchExtractor {

    List<RepositoryNameAndUrl> extract(
        String token,
        String username,
        String keyword,
        Pageable pageable
    );
}
