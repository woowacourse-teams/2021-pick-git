package com.woowacourse.s3proxy.web.infrastructure;

import static java.util.stream.Collectors.toList;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.woowacourse.s3proxy.exception.UploadFailException;
import com.woowacourse.s3proxy.web.domain.PickGitStorage;
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
            .map(multipartFile -> upload(multipartFile, userName))
            .collect(toList());
    }

    private StoreResult upload(MultipartFile multipartFile, String userName) {
        try {
            ObjectMetadata objectMetadata = createObjectMetadata(multipartFile);
            putObjectToS3(multipartFile, userName, objectMetadata);

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

    private void putObjectToS3(MultipartFile multipartFile, String userName, ObjectMetadata objectMetadata)
        throws IOException {
        s3Client.putObject(
            bucket,
            fileNameGenerator.generate(multipartFile, userName),
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
