package com.woowacourse.s3_proxy.web.infrastructure;

import static java.util.stream.Collectors.toList;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.woowacourse.s3_proxy.exception.upload.UploadFailureException;
import com.woowacourse.s3_proxy.web.domain.PickGitStorage;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class S3Storage implements PickGitStorage {

    @Value("${aws.s3.bucket_name}")
    private String bucket;

    @Value("${aws.cloud_front.file_url_format}")
    private String fileUrlFormat;

    private final FileNameGenerator fileNameGenerator;

    private final AmazonS3 s3Client;

    public S3Storage(FileNameGenerator fileNameGenerator, AmazonS3 s3Client) {
        this.fileNameGenerator = fileNameGenerator;
        this.s3Client = s3Client;
    }

    @Override
    public List<StoreResult> store(List<MultipartFile> multipartFiles, String userName) {
        return multipartFiles.stream()
            .map(multipartFile -> upload(
                multipartFile,
                fileNameGenerator.generate(multipartFile, userName)
            )).collect(toList());
    }

    private StoreResult upload(MultipartFile multipartFile, String originalFileName) {
        try {
            ObjectMetadata objectMetadata = createObjectMetadata(multipartFile);
            putObjectToS3(multipartFile, originalFileName, objectMetadata);

            return new PickGitStorage.StoreResult(
                originalFileName,
                String.format(fileUrlFormat, originalFileName)
            );
        } catch (Exception e) {
            throw new UploadFailureException(e);
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
