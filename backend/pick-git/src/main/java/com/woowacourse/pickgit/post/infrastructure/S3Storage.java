package com.woowacourse.pickgit.post.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.bind.v2.model.core.TypeRef;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.post.presentation.PickGitStorage;
import java.io.File;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoProperties.Storage;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Repository
@Profile("!test")
public class S3Storage implements PickGitStorage {

    private static final String MULTIPART_KEY = "files";

    private final RestClient restClient;

    @Value("${storage.pickgit.s3proxy}")
    private String s3ProxyUrl;

    public S3Storage(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<String> store(List<File> files, String userName) {
        StorageDto response = restClient
            .postForEntity(s3ProxyUrl, createBody(files, userName), StorageDto.class)
            .getBody();

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
