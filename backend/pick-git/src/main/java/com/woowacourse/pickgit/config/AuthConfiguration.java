package com.woowacourse.pickgit.config;

import com.woowacourse.pickgit.common.JwtXXX;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfiguration {

    @Bean
    public JwtXXX jwt() {
        return new JwtXXX();
    }
}
