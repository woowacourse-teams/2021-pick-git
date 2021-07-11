package com.woowacourse.pickgit.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserRepositoryTest {

    private static final String NAME = "yjksw";
    private static final String IMAGE = "http://img.com";
    private static final String DESCRIPTION = "The Best";
    private static final String GITHUB_URL = "www.github.com/yjksw";
    private static final String COMPANY = "woowacourse";
    private static final String LOCATION = "Seoul";
    private static final String WEBSITE = "www.pick-git.com";
    private static final String TWITTER = "pick-git twitter";

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        BasicProfile basicProfile = new BasicProfile(NAME, IMAGE, DESCRIPTION);
        GithubProfile githubProfile = new GithubProfile(GITHUB_URL, COMPANY, LOCATION, WEBSITE,
            TWITTER);
        User user = new User(basicProfile, githubProfile);

        userRepository.save(user);
    }

    @DisplayName("유저 이름으로 User 엔티티를 조회한다.")
    @Test
    void findUserByBasicProfile_Name_saveUser_Success() {
        User user = userRepository.findByBasicProfile_Name(NAME).get();
        assertThat(user.getId()).isNotNull();
        assertThat(user.getName()).isEqualTo(NAME);
        assertThat(user.getImage()).isEqualTo(IMAGE);
        assertThat(user.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(user.getGithubUrl()).isEqualTo(GITHUB_URL);
        assertThat(user.getCompany()).isEqualTo(COMPANY);
        assertThat(user.getLocation()).isEqualTo(LOCATION);
        assertThat(user.getWebsite()).isEqualTo(WEBSITE);
        assertThat(user.getTwitter()).isEqualTo(TWITTER);
    }
}
