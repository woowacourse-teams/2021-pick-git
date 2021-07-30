package com.woowacourse.pickgit.unit.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private List<User> users;

    @BeforeEach
    void setUp() {
        users = UserFactory.mockSearchUsersWithId();
        users
            .stream()
            .forEach(user -> userRepository.save(user));
        entityManager.flush();
        entityManager.clear();
    }

    @DisplayName("유저 이름으로 유저를 조회한다.")
    @Test
    void findUserByBasicProfile_Name_ValidUserName_Success() {
        // when
        User user = userRepository
            .findByBasicProfile_Name(users.get(0).getName())
            .orElseThrow(InvalidUserException::new);

        // then
        assertThat(user)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(users.get(0));
    }

    @DisplayName("등록되지 않은 유저 이름으로 유저를 조회할 수 없다.- 400 예외")
    @Test
    void findUserByBasicProfile_Name_InvalidUserName_400Exception() {
        // when
        assertThatThrownBy(() -> {
            userRepository
                .findByBasicProfile_Name("invalidUser")
                .orElseThrow(InvalidUserException::new);
        }).isInstanceOf(InvalidUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("유효하지 않은 유저입니다.");
    }

    @DisplayName("저장된 유저중 유사한 이름을 가진 유저를 검색할 수 있다.")
    @Test
    void findAllByUsername_SearchUserByUsername_Success() {
        // given
        String searchKeyword = "bing";

        // when
        Pageable pageable = PageRequest.of(0, 3);
        List<User> searchResult = userRepository.findAllByUsername(searchKeyword, pageable);

        // then
        assertThat(searchResult).hasSize(3);
        assertThat(searchResult)
            .extracting("name")
            .containsExactly(
                users.get(0).getName(),
                users.get(1).getName(),
                users.get(2).getName()
            );
    }

    @DisplayName("저장된 유저중 유사한 이름을 가진 4, 5번째 유저를 검색할 수 있다.")
    @Test
    void findAllByUsername_SearchThirdAndFourthUserByUsername_Success() {
        // given
        String seachKeyword = "bing";

        // when
        Pageable pageable = PageRequest.of(1, 3);
        List<User> searchResult = userRepository.findAllByUsername(seachKeyword, pageable);

        // then
        assertThat(searchResult).hasSize(2);
        assertThat(searchResult)
            .extracting("name")
            .containsExactly(
                users.get(3).getName(),
                users.get(4).getName()
            );
    }

    @DisplayName("저장된 유저중 검색 키워드와 유사한 유저가 없으면 빈 값을 반환한다.")
    @Test
    void findAllByUsername_SearchNoMatchedUser_Success() {
        // given
        String searchKeyword = "woowatech";

        // when
        Pageable pageable = PageRequest.of(0, 3);
        List<User> searchResult = userRepository.findAllByUsername(searchKeyword, pageable);

        // then
        assertThat(searchResult).hasSize(0);
    }
}
