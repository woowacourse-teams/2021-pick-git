package com.woowacourse.pickgit.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.user.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private UserFactory userFactory;

    @BeforeEach
    void setUp() {
        this.userFactory = new UserFactory();
        userRepository.save(userFactory.user());
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
            .isEqualTo(userFactory.user());
    }

    //TODO follow
    //TODO unfollow
}
