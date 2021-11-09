package com.woowacourse.pickgit.user.infrastructure;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.post.domain.util.RestClient;
import com.woowacourse.pickgit.post.infrastructure.S3Storage.StorageDto;
import com.woowacourse.pickgit.user.domain.profile.PickGitProfileStorage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Profile("!test")
@Component
public class S3PickGitProfileStorage implements PickGitProfileStorage {

    private static final String MULTIPART_KEY = "files";

    private final RestClient restClient;
    private final String s3ProxyUrl;

    public S3PickGitProfileStorage(
        RestClient restClient,
        @Value("${storage.pickgit.s3proxy}") String s3ProxyUrl
    ) {
        this.restClient = restClient;
        this.s3ProxyUrl = s3ProxyUrl;
    }

    @Override
    public Optional<String> store(File file, String userName) {
        StorageDto response = restClient
            .postForEntity(s3ProxyUrl, createBody(List.of(file), userName), StorageDto.class)
            .getBody();
        if (Objects.isNull(response)) {
            throw new PlatformHttpErrorException();
        }
        List<String> imageUrls = response.getUrls();
        return Optional.ofNullable(imageUrls.get(0));
    }

    private MultiValueMap<String, Object> createBody(
        List<File> files,
        String userName
    ) {
        MultiValueMap<String, Object> body = createMultipartMap(files);
        body.add("userName", userName);
        return body;
    }

    private MultiValueMap<String, Object> createMultipartMap(List<File> files) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        files.forEach(file -> body.add(MULTIPART_KEY, new FileSystemResource(file)));

        return body;
    }

    @Override
    public String storeByteFile(byte[] byteFile, String userName) {
        return saveImageAndGetUrl(byteFile, userName);
    }

    private String saveImageAndGetUrl(byte[] imageSource, String username) {
        File imgFile = fileFrom(imageSource);
        return store(imgFile, username)
            .orElseThrow(PlatformHttpErrorException::new);
    }

    private File fileFrom(byte[] image) {
        try {
            File tempFile = File.createTempFile("temp", null, null);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(image);
                return tempFile;
            }
        } catch (IOException e) {
            throw new PlatformHttpErrorException();
        }
    }
}
