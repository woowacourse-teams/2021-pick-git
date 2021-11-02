package com.woowacourse.pickgit.acceptance.post.github;

import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.post.application.dto.response.RepositoryResponseDto;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PostRepositorySearchAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setUp() {
        toRead();
    }

    @DisplayName("사용자는 Repository 목록을 키워드 검색으로 가져올 수 있다.")
    @Test
    void searchUserRepositories_LoginUser_Success() {
        List<RepositoryResponseDto> response  =
            NEOZAL.은로그인을하고().레포지토리_목록을_키워드로_가져온다("woowa").as(new TypeRef<>() {
        });

        assertThat(response).hasSize(2);
    }

    @DisplayName("레포지토리 검색 시 토큰이 없는 경우 예외가 발생한다. - 401 예외")
    @Test
    void searchUserRepositories_InvalidAccessToken_401Exception() {
        ApiErrorResponse response = GUEST.는().비정상토큰으로_레포지토리_목록을_키워드로_가져온다("woowa")
            .as(ApiErrorResponse.class);

        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("사용자는 Repository 목록을 가져올 수 있다.")
    @Test
    void userRepositories_LoginUser_Success() {
        // given
        List<RepositoryResponseDto> response =
            NEOZAL.은로그인을하고().레포지토리_목록을_가져온다().as(new TypeRef<>() {
            });

        assertThat(response).hasSize(2);
    }

    @DisplayName("토큰이 유효하지 않은 경우 Repository 목록을 가져오는데 예외가 발생한다. - 500 예외")
    @Test
    void userRepositories_InvalidAccessToken_500Exception() {
        int statusCode = GUEST.는().비정상토큰으로_레포지토리목록을_가져온다().statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
