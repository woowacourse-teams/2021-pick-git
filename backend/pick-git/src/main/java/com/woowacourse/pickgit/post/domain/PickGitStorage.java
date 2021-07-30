package com.woowacourse.pickgit.post.domain;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface PickGitStorage {

    List<String> store(List<File> files, String userName);

    Optional<String> store(File file, String userName);
}
