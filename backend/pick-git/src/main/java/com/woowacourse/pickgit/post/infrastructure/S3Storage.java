package com.woowacourse.pickgit.post.infrastructure;

import com.woowacourse.pickgit.post.presentation.PickGitStorage;
import java.io.File;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Repository
@Profile("!test")
public class S3Storage implements PickGitStorage {
    private static final String MULTIPART_KEY = "files";

    @Value("${storage.pickgit.s3proxy}")
    private String s3ProxyUrl;

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<String> store(List<File> files, String userName) {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<List> listResponseEntity = restTemplate
            .postForEntity(s3ProxyUrl, createBody(files, userName), List.class);

        return (List<String>) listResponseEntity.getBody();
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
}
