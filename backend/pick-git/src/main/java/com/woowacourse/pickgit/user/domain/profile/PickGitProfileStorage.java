package com.woowacourse.pickgit.user.domain.profile;

import java.io.File;
import java.util.Optional;

public interface PickGitProfileStorage {

    Optional<String> store(File file, String userName);
    String storeByteFile(byte[] file, String userName);
}
