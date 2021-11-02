package com.woowacourse.pickgit.acceptance.user.follow;

import static com.woowacourse.pickgit.common.fixture.TUser.DANI;
import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.KEVIN;
import static com.woowacourse.pickgit.common.fixture.TUser.KODA;
import static com.woowacourse.pickgit.common.fixture.TUser.MARK;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static com.woowacourse.pickgit.common.fixture.TUser.모든유저;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.user.presentation.dto.response.UserSearchResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserFollowReadAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setUp() {
        toRead();
        모든유저().로그인을한다();

        NEOZAL.은로그인을하고().팔로우를한다(KODA, DANI);
        KODA.은로그인을하고().팔로우를한다(NEOZAL, MARK, DANI);
        MARK.은로그인을하고().팔로우를한다(KODA, DANI);
        DANI.은로그인을하고().팔로우를한다(KEVIN);
    }

    @DisplayName("로그인 - 특정 유저의 팔로잉 목록을 조회한다. (팔로잉 여부 true/false, 본인은 null)")
    @Test
    void searchFollowings_Login_FollowingVarious() {
        // given
        List<UserSearchResponse> 코다의팔로우 = NEOZAL.은로그인을하고().팔로우를확인한다(KODA);

        // then
        assertThat(코다의팔로우)
            .extracting("username", "following")
            .containsExactly(
                tuple(NEOZAL.name(), null),
                tuple(MARK.name(), false),
                tuple(DANI.name(), true)
            ).hasSize(3);
    }

    @DisplayName("비로그인 - 특정 유저의 팔로잉 목록을 조회한다. (팔로잉 여부 모두 null)")
    @Test
    void searchFollowings_Guest_FollowingNull() {

        List<UserSearchResponse> 코다의팔로우 = GUEST.는().팔로잉를확인한다(KODA);
        // then
        assertThat(코다의팔로우)
            .extracting("username", "following")
            .containsExactly(
                tuple(NEOZAL.name(), null),
                tuple(MARK.name(), null),
                tuple(DANI.name(), null)
            ).hasSize(3);
    }

    @DisplayName("로그인 - 특정 유저의 팔로워 목록을 조회한다. (팔로잉 여부 true/false, 본인은 null)")
    @Test
    void searchFollowers_Login_FollowingVarious() {
        // given
        List<UserSearchResponse> 다니의팔로워 = NEOZAL.은로그인을하고().팔로워를확인한다(DANI);

        // then
        assertThat(다니의팔로워)
            .extracting("username", "following")
            .containsExactly(
                tuple(NEOZAL.name(), null),
                tuple(KODA.name(), true),
                tuple(MARK.name(), false)
            ).hasSize(3);
    }

    @DisplayName("비로그인 - 특정 유저의 팔로워 목록을 조회한다. (팔로잉 여부 모두 null)")
    @Test
    void searchFollowers_Guest_FollowingNull() {
        // given
        List<UserSearchResponse> 다니의팔로워 = GUEST.는().팔로워를확인한다(DANI);

        // then
        assertThat(다니의팔로워)
            .extracting("username", "following")
            .containsExactly(
                tuple(NEOZAL.name(), null),
                tuple(KODA.name(), null),
                tuple(MARK.name(), null)
            ).hasSize(3);
    }
}
