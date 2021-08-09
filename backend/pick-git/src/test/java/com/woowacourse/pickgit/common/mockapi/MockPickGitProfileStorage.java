package com.woowacourse.pickgit.common.mockapi;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.user.domain.profile.PickGitProfileStorage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class MockPickGitProfileStorage implements PickGitProfileStorage {

    @Override
    public Optional<String> store(File file, String userName) {
        return Optional.ofNullable(file.getName());
    }

    @Override
    public String storeByteFile(byte[] file, String userName) {
        return fileFrom(file).getName();
    }

    private File fileFrom(byte[] image) {
        try {
            Path path = Files.write(
                Files.createTempFile(null, null),
                image
            );

            return path.toFile();
        } catch (IOException e) {
            throw new PlatformHttpErrorException();
        }
    }
}
