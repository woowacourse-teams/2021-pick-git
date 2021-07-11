package com.woowacourse.s3proxy.web.infrastructure;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.woowacourse.s3proxy.exception.UploadFailException;
import com.woowacourse.s3proxy.web.domain.PickGitStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Repository
public class S3Storage implements PickGitStorage {

    @Value("${aws.s3.bucket_name}")
    private String bucket;

    @Value("${aws.cloud_front.file_url_format}")
    private String fileUrlFormat;

    private final AmazonS3 s3Client;

    public S3Storage(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public List<StoreResult> store(List<MultipartFile> multipartFiles) {
        return multipartFiles.stream()
            .map(this::upload)
            .collect(toList());
    }

    private StoreResult upload(MultipartFile multipartFile) {
        try {
            ObjectMetadata objectMetadata = createObjectMetadata(multipartFile);
            putObjectToS3(multipartFile, objectMetadata);

            return new PickGitStorage.StoreResult(
                multipartFile.getOriginalFilename(),
                String.format(fileUrlFormat, multipartFile.getOriginalFilename())
            );
        } catch (Exception e) {
            return new PickGitStorage.StoreResult(
                multipartFile.getOriginalFilename(),
                new UploadFailException(e)
            );
        }
    }

    private void putObjectToS3(MultipartFile multipartFile, ObjectMetadata objectMetadata)
        throws IOException {
        s3Client.putObject(
            bucket,
            multipartFile.getOriginalFilename(),
            multipartFile.getInputStream(),
            objectMetadata
        );
    }

    private ObjectMetadata createObjectMetadata(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());
        return objectMetadata;
    }
}
