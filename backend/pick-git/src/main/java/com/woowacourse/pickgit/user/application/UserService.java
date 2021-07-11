package com.woowacourse.pickgit.user.application;

import com.woowacourse.pickgit.user.application.dto.AuthUserServiceDto;
import com.woowacourse.pickgit.user.application.dto.UserProfileServiceDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileServiceDto getAuthUserProfile(AuthUserServiceDto authUserServiceDto) {
        return getUserProfile(authUserServiceDto.getGithubName());
    }

    public UserProfileServiceDto getUserProfile(String username) {
        User user = userRepository.findByBasicProfile_Name(username).get();
        UserProfileServiceDto userProfileDto = new UserProfileServiceDto(
            user.getName(), user.getImage(), user.getDescription(),
            user.getFollowerCount(), user.getFollowingCount(), user.getPostCount(),
            user.getGithubUrl(), user.getCompany(), user.getLocation(),
            user.getWebsite(), user.getTwitter()
        );

        return userProfileDto;
    }
}
