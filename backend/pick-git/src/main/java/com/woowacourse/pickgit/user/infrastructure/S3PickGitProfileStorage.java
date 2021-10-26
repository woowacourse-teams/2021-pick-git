package com.woowacourse.pickgit.user.infrastructure;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.post.infrastructure.S3Storage.StorageDto;
import com.woowacourse.pickgit.user.domain.profile.PickGitProfileStorage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Profile("!test")
@Component
public class S3PickGitProfileStorage implements PickGitProfileStorage {

    private static final String MULTIPART_KEY = "files";

    private final WebClient webClient;
    private final String s3ProxyUrl;

    public S3PickGitProfileStorage(
        WebClient webClient,
        @Value("${storage.pickgit.s3proxy}") String s3ProxyUrl
    ) {
        this.webClient = webClient;
        this.s3ProxyUrl = s3ProxyUrl;
    }

    @Override
    public Optional<String> store(File file, String userName) {
        List<String> imageUrls = webClient.post()
            .uri(s3ProxyUrl)
            .bodyValue(createBody(List.of(file), userName))
            .retrieve()
            .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                .flatMap(errorMessage -> Mono.error(new PlatformHttpErrorException(errorMessage))))
            .bodyToMono(StorageDto.class)
            .blockOptional()
            .orElseThrow(PlatformHttpErrorException::new)
            .getUrls();

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
