package com.woowacourse.pickgit.common.mockapi;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.post.domain.PickGitStorage;
import java.io.File;
import java.util.List;
import java.util.Optional;

public class MockPickGitStorage implements PickGitStorage {

    @Override
    public List<String> store(List<File> files, String userName) {
        return files.stream()
            .map(File::getName)
            .collect(toList());
    }

    @Override
    public Optional<String> store(File file, String userName) {
        return Optional.ofNullable(file.getName());
    }
}
