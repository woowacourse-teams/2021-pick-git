package com.woowacourse.pickgit.post.application.dto.request;

import lombok.Builder;
import org.springframework.data.domain.Pageable;

@Builder
public class SearchRepositoryRequestDto {

    private String token;
    private String username;
    private String keyword;
    private Pageable pageable;

    private SearchRepositoryRequestDto() {
    }

    public SearchRepositoryRequestDto(
        String token,
        String username,
        String keyword,
        Pageable pageable
    ) {
        this.token = token;
        this.username = username;
        this.keyword = keyword;
        this.pageable = pageable;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getKeyword() {
        return keyword;
    }

    public Pageable getPageable() {
        return pageable;
    }
}
