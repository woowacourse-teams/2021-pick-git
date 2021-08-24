package com.woowacourse.pickgit.common.mockapi;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.post.domain.repository.PickGitStorage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import org.springframework.web.multipart.MultipartFile;

public class MockPickGitStorage implements PickGitStorage {

    @Override
    public List<String> store(List<File> files, String userName) {
        return files.stream()
            .map(File::getName)
            .collect(toList());
    }

    @Override
    public List<String> storeMultipartFile(List<MultipartFile> multipartFiles, String userName) {
        return store(toFiles(multipartFiles), userName);
    }

    private List<File> toFiles(List<MultipartFile> files) {
        return files.stream()
            .map(toFile())
            .collect(toList());
    }

    private Function<MultipartFile, File> toFile() {
        return multipartFile -> {
            try {
                return multipartFile.getResource().getFile();
            } catch (IOException e) {
                return tryCreateTempFile(multipartFile);
            }
        };
    }

    private File tryCreateTempFile(MultipartFile multipartFile) {
        try {
            Path tempFile = Files.createTempFile(null, null);
            Files.write(tempFile, multipartFile.getBytes());

            return tempFile.toFile();
        } catch (IOException ioException) {
            throw new PlatformHttpErrorException();
        }
    }
}
