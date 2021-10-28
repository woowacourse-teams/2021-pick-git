package com.woowacourse.pickgit.acceptance.user;

import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.KEVIN;
import static com.woowacourse.pickgit.common.fixture.TUser.KODA;
import static com.woowacourse.pickgit.common.fixture.TUser.MARK;
import static com.woowacourse.pickgit.common.fixture.TUser.모든유저;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserSearchAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setUp() {
        모든유저().로그인을한다();
    }

    @DisplayName("로그인 - 저장된 유저중 유사한 이름을 가진 유저를 검색할 수 있다. 단, 자기 자신은 검색되지 않는다.(팔로잉 여부 true/false)")
    @Test
    void searchUser_LoginUser_Success() {
        List<UserSearchResponseDto> response = KEVIN.은로그인을하고().유저를_검색한다("K")
            .as(new TypeRef<>() {
            });

        assertThat(response)
            .hasSize(2)
            .extracting("username", "following")
            .containsExactlyInAnyOrder(
                tuple(MARK.name(), false),
                tuple(KODA.name(), false)
            );
    }

    @DisplayName("비 로그인 - 저장된 유저중 유사한 이름을 가진 유저를 검색할 수 있다. (팔로잉 필드 null)")
    @Test
    void searchUser_GuestUser_Success() {
        List<UserSearchResponseDto> response = GUEST.는().유저를_검색한다("K")
            .as(new TypeRef<>() {
            });

        assertThat(response)
            .hasSize(3)
            .extracting("username", "following")
            .containsExactlyInAnyOrder(
                tuple(MARK.name(), null),
                tuple(KEVIN.name(), null),
                tuple(KODA.name(), null)
            );
    }
}
