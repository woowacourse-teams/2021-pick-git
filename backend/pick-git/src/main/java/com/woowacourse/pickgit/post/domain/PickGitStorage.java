package com.woowacourse.pickgit.post.domain;

import java.io.File;
import java.util.List;

public interface PickGitStorage {

    List<String> store(List<File> files, String userName);
}
