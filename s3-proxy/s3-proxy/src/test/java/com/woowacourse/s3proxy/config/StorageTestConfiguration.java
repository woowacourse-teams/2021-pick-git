package com.woowacourse.s3proxy.config;

import cloud.localstack.awssdkv1.TestUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.woowacourse.s3proxy.common.fileValidator.ImageFileValidator;
import java.awt.Image;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
public class StorageTestConfiguration {

    @Value("${aws.s3.bucket_name}")
    private String bucket;

    @Bean
    public AmazonS3 amazonS3() {
        AmazonS3 amazonS3 = TestUtils.getClientS3();
        amazonS3.createBucket(bucket);

        return amazonS3;
    }
}
