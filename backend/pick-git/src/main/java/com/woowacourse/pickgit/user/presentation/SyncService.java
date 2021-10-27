package com.woowacourse.pickgit.user.presentation;

import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Profile("dev")
@Service
public class SyncService {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void sync() {
        List<User> all = userRepository.findAll();
        for (User user : all) {
            Long userId = user.getId();
            String userName = user.getName();
            String image = user.getImage();
            String description = user.getDescription();
            BasicProfile basicProfile = new BasicProfile(userName, image, description);

            String githubUrl1 = user.getGithubUrl();
            String company1 = user.getCompany();
            String location1 = user.getLocation();
            String website1 = user.getWebsite();
            String twitter1 = user.getTwitter();
            GithubProfile githubProfile = new GithubProfile(githubUrl1, company1, location1,
                website1, twitter1);
            User tmp = new User(userId, basicProfile, githubProfile);

            elasticsearchOperations.save(tmp);
        }
    }
}
