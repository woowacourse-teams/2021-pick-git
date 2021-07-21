package com.woowacourse.pickgit.unit.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

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

    @DisplayName("유저 이름으로 User 엔티티를 조회한다.")
    @Test
    void findUserByBasicProfile_Name_saveUser_Success() {
        User actualUser = userRepository
            .findByBasicProfile_Name("yjksw")
            .get();

        assertThat(actualUser)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(UserFactory.user());
    }
}
