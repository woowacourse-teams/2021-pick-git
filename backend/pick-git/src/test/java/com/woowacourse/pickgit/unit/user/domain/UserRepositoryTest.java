package com.woowacourse.pickgit.unit.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.http.HttpStatus;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void setUp() {
        userRepository.save(UserFactory.user());
        testEntityManager.flush();
        testEntityManager.clear();
    }

    @DisplayName("유저 이름으로 유저를 조회한다.")
    @Test
    void findUserByBasicProfile_Name_ValidUserName_Success() {
        // when
        User user = userRepository
            .findByBasicProfile_Name("testUser")
            .orElseThrow(InvalidUserException::new);

        // then
        assertThat(user)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(UserFactory.user());
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
}
