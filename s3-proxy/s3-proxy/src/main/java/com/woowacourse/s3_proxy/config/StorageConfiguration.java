package com.woowacourse.s3_proxy.config;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@Configuration
public class StorageConfiguration {

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder
            .standard()
            .withCredentials(InstanceProfileCredentialsProvider.getInstance())
            .withRegion(Regions.AP_NORTHEAST_2)
            .build();
    }
}
