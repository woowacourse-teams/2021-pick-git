package com.woowacourse.pickgit.post.application.dto.request;

import lombok.Builder;
import org.springframework.data.domain.Pageable;

@Builder
public class RepositoryRequestDto {

    private String token;
    private String username;
    private Pageable pageable;

    private RepositoryRequestDto() {
    }

    public RepositoryRequestDto(String token, String username, Pageable pageable) {
        this.token = token;
        this.username = username;
        this.pageable = pageable;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public Pageable getPageable() {
        return pageable;
    }
}
