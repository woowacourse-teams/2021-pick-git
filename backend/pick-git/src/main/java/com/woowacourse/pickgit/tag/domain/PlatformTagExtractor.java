package com.woowacourse.pickgit.tag.domain;

import java.util.List;

public interface PlatformTagExtractor {

    List<String> extractTags(String accessToken, String userName, String repositoryName);
}
