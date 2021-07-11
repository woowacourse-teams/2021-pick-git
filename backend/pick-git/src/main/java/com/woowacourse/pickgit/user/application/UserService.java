package com.woowacourse.pickgit.user.application;

import com.woowacourse.pickgit.user.application.dto.AuthUserServiceDto;
import com.woowacourse.pickgit.user.application.dto.FollowServiceDto;
import com.woowacourse.pickgit.user.application.dto.UserProfileServiceDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.exception.InvalidUserException;
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
        User user = findUserByName(username);

        return new UserProfileServiceDto(
            user.getName(), user.getImage(), user.getDescription(),
            user.getFollowerCount(), user.getFollowingCount(), user.getPostCount(),
            user.getGithubUrl(), user.getCompany(), user.getLocation(),
            user.getWebsite(), user.getTwitter()
        );
    }

    public FollowServiceDto followUser(AuthUserServiceDto authUserServiceDto,
        String targetUsername) {
        User source = findUserByName(authUserServiceDto.getGithubName());
        User target = findUserByName(targetUsername);

        source.follow(target);

        return new FollowServiceDto(target.getFollowerCount(), true);
    }

    public FollowServiceDto unfollowUser(AuthUserServiceDto authUserServiceDto,
        String targetUsername) {
        User source = findUserByName(authUserServiceDto.getGithubName());
        User target = findUserByName(targetUsername);

        source.unfollow(target);

        return new FollowServiceDto(target.getFollowerCount(), false);
    }

    private User findUserByName(String githubName) {
        return userRepository
            .findByBasicProfile_Name(githubName)
            .orElseThrow(InvalidUserException::new);
    }
}
