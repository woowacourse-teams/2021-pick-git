package com.woowacourse.pickgit.post.infrastructure;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.post.domain.repository.PickGitStorage;
import com.woowacourse.pickgit.post.domain.util.RestClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

@Profile("!test")
@Repository
public class S3Storage implements PickGitStorage {

    private static final String MULTIPART_KEY = "files";

    private final RestClient restClient;
    private final String s3ProxyUrl;

    public S3Storage(
        RestClient restClient,
        @Value("${storage.pickgit.s3proxy}") String s3ProxyUrl
    ) {
        this.restClient = restClient;
        this.s3ProxyUrl = s3ProxyUrl;
    }

    @Override
    public List<String> store(List<File> files, String userName) {
        StorageDto response = restClient
            .postForEntity(s3ProxyUrl, createBody(files, userName), StorageDto.class)
            .getBody();

        if (Objects.isNull(response)) {
            throw new PlatformHttpErrorException();
        }
        return response.getUrls();
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
            File tempFile = File.createTempFile("temp", null, null);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(multipartFile.getBytes());
                return tempFile;
            }
        } catch (IOException e) {
            throw new PlatformHttpErrorException();
        }
    }

    public static class StorageDto {

        private List<String> urls;

        private StorageDto() {
        }

        public StorageDto(List<String> urls) {
            this.urls = urls;
        }

        public List<String> getUrls() {
            return urls;
        }
    }
}
