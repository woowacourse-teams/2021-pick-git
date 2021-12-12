package com.woowacourse.pickgit.post.infrastructure;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.exception.upload.upload.UploadFailureException;
import com.woowacourse.pickgit.post.domain.repository.PickGitStorage;
import com.woowacourse.pickgit.post.domain.util.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Profile("!test")
@Repository
public class LocalS3Storage implements PickGitStorage {

    private String bucket;

    private String fileUrlFormat;

    private final FileNameGenerator fileNameGenerator;

    private final AmazonS3 s3Client;

    public LocalS3Storage(@Value("${aws.s3.bucket_name}") String bucket,
                          @Value("${aws.cloud_front.file_url_format}") String fileUrlFormat,
                          FileNameGenerator fileNameGenerator,
                          AmazonS3 s3Client
    ) {
        this.bucket = bucket;
        this.fileUrlFormat = fileUrlFormat;
        this.fileNameGenerator = fileNameGenerator;
        this.s3Client = s3Client;
    }

    @Override
    public List<String> storeMultipartFile(List<MultipartFile> multipartFiles, String userName) {
        return multipartFiles.stream()
                .map(multipartFile -> upload(
                        multipartFile,
                        fileNameGenerator.generate(multipartFile, userName)
                )).collect(toList());
    }

    @Override
    public List<String> store(List<File> files, String userName) {
        throw new UnsupportedOperationException();
    }

    private String upload(MultipartFile multipartFile, String originalFileName) {
        try {
            ObjectMetadata objectMetadata = createObjectMetadata(multipartFile);
            putObjectToS3(multipartFile, originalFileName, objectMetadata);

            return String.format(fileUrlFormat, originalFileName);
        } catch (Exception e) {
            throw new UploadFailureException();
        }
    }

    private ObjectMetadata createObjectMetadata(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());
        return objectMetadata;
    }

    private void putObjectToS3(MultipartFile multipartFile, String originalFileName, ObjectMetadata objectMetadata)
            throws IOException {
        s3Client.putObject(
                bucket,
                originalFileName,
                multipartFile.getInputStream(),
                objectMetadata
        );
    }
}
