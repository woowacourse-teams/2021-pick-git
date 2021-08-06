package com.woowacourse.pickgit.post.domain.util;

import com.woowacourse.pickgit.post.domain.util.dto.RepositoryNameAndUrl;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface PlatformRepositoryExtractor {

    List<RepositoryNameAndUrl> extract(String token, String username, Pageable pageable);
}
