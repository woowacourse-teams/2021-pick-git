package com.woowacourse.pickgit.query.acceptance.post;

import static com.woowacourse.pickgit.query.fixture.TPost.KEVINPOST;
import static com.woowacourse.pickgit.query.fixture.TPost.MARKPOST;
import static com.woowacourse.pickgit.query.fixture.TPost.NEOZALPOST;
import static com.woowacourse.pickgit.query.fixture.TUser.DANI;
import static com.woowacourse.pickgit.query.fixture.TUser.KEVIN;
import static com.woowacourse.pickgit.query.fixture.TUser.KODA;
import static com.woowacourse.pickgit.query.fixture.TUser.MARK;
import static com.woowacourse.pickgit.query.fixture.TUser.NEOZAL;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class PostAcceptanceTest_Query extends AcceptanceTest {

    @BeforeEach
    void setUp() {
        toRead();
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        MARK.은로그인을하고().포스트를등록한다(MARKPOST);
        KEVIN.은로그인을하고().포스트를등록한다(KEVINPOST);

        MARK.은로그인을하고().포스트에좋아요를누른다(MARKPOST);
        MARK.은로그인을하고().포스트에좋아요를누른다(KEVINPOST);
        KODA.은로그인을하고().포스트에좋아요를누른다(MARKPOST);
        DANI.은로그인을하고().포스트에좋아요를누른다(KEVINPOST);
    }

    @DisplayName("로그인일때 홈 피드를 조회한다. - 게시글 좋아요 여부 true/false")
    @Test
    void readHomeFeed_LoginUser_Success() {
        String token = NEOZAL.은로그인을한다();

        List<PostResponseDto> response = given().log().all()
            .auth().oauth2(token)
            .when()
            .get("/api/posts?page=0&limit=3")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(new TypeRef<>() {
            });

        assertThat(response)
            .hasSize(1)
            .extracting("liked")
            .containsExactly(false);
    }

    @DisplayName("비 로그인이어도 홈 피드 조회가 가능하다. - 게시물 좋아요 여부는 항상 null")
    @Test
    void read_GuestUser_Success() {
        List<PostResponseDto> response = given().log().all()
            .when()
            .get("/api/posts?page=0&limit=3")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(new TypeRef<>() {
            });

        assertThat(response)
            .hasSize(3)
            .extracting("liked")
            .containsExactly(null, null, null);
    }

    @DisplayName("로그인 상태에서 내 피드 조회가 가능하다.")
    @Test
    void readMyFeed_LoginUser_Success() {
        String token = NEOZAL.은로그인을한다();

        List<PostResponseDto> response = given().log().all()
            .auth().oauth2(token)
            .when()
            .get("/api/posts/me?page=0&limit=3")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(new TypeRef<>() {
            });

        assertThat(response)
            .hasSize(1)
            .extracting("liked")
            .containsExactly(false);
    }

    @DisplayName("비로그인 상태에서는 내 피드 조회가 불가능하다.")
    @Test
    void readMyFeed_GuestUser_Success() {
        given().log().all()
            .when()
            .get("/api/posts/me?page=0&limit=3")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("로그인 상태에서 다른 유저 피드 조회가 가능하다.")
    @Test
    void readUserFeed_LoginUser_Success() {
        String loginUserToken = NEOZAL.은로그인을한다();

        List<PostResponseDto> response = given().log().all()
            .auth().oauth2(loginUserToken)
            .when()
            .get("/api/posts/" + MARK.name() + "?page=0&limit=3")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(new TypeRef<>() {
            });

        assertThat(response)
            .hasSize(1)
            .extracting("liked")
            .containsExactly(false);
    }

    @DisplayName("비 로그인 상태에서 다른 유저 피드 조회가 가능하다.")
    @Test
    void readUserFeed_GuestUser_Success() {
        List<PostResponseDto> response = given().log().all()
            .when()
            .get("/api/posts/" + MARK.name() + "?page=0&limit=3")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(new TypeRef<>() {
            });

        assertThat(response)
            .hasSize(1);
    }
}
