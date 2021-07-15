package com.woowacourse.pickgit.user.application;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.user.application.dto.AuthUserServiceDto;
import com.woowacourse.pickgit.user.application.dto.FollowServiceDto;
import com.woowacourse.pickgit.user.application.dto.UserProfileServiceDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.exception.user.SameSourceTargetUserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileServiceDto getMyUserProfile(AuthUserServiceDto authUserServiceDto) {
        User user = findUserByName(authUserServiceDto.getGithubName());

        return new UserProfileServiceDto(
            user.getName(), user.getImage(), user.getDescription(),
            user.getFollowerCount(), user.getFollowingCount(), user.getPostCount(),
            user.getGithubUrl(), user.getCompany(), user.getLocation(),
            user.getWebsite(), user.getTwitter(), null
        );
    }

    @Transactional(readOnly = true)
    public UserProfileServiceDto getUserProfile(AppUser appUser, String targetUsername) {
        User targetUser = findUserByName(targetUsername);

        if (appUser.isGuest()) {
            return new UserProfileServiceDto(
                targetUser.getName(), targetUser.getImage(), targetUser.getDescription(),
                targetUser.getFollowerCount(), targetUser.getFollowingCount(), targetUser.getPostCount(),
                targetUser.getGithubUrl(), targetUser.getCompany(), targetUser.getLocation(),
                targetUser.getWebsite(), targetUser.getTwitter(), null
            );
        }

        User sourceUser = findUserByName(appUser.getUsername());

        return new UserProfileServiceDto(
            targetUser.getName(), targetUser.getImage(), targetUser.getDescription(),
            targetUser.getFollowerCount(), targetUser.getFollowingCount(), targetUser.getPostCount(),
            targetUser.getGithubUrl(), targetUser.getCompany(), targetUser.getLocation(),
            targetUser.getWebsite(), targetUser.getTwitter(), sourceUser.isFollowing(targetUser)
        );
    }

    public FollowServiceDto followUser(AuthUserServiceDto authUserServiceDto,
        String targetUsername) {
        User source = findUserByName(authUserServiceDto.getGithubName());
        User target = findUserByName(targetUsername);

        validateDifferentSourceTarget(source, target);
        source.follow(target);

        return new FollowServiceDto(target.getFollowerCount(), true);
    }

    public FollowServiceDto unfollowUser(AuthUserServiceDto authUserServiceDto,
        String targetUsername) {
        User source = findUserByName(authUserServiceDto.getGithubName());
        User target = findUserByName(targetUsername);

        validateDifferentSourceTarget(source, target);
        source.unfollow(target);

        return new FollowServiceDto(target.getFollowerCount(), false);
    }

    private User findUserByName(String githubName) {
        return userRepository
            .findByBasicProfile_Name(githubName)
            .orElseThrow(InvalidUserException::new);
    }

    private void validateDifferentSourceTarget(User source, User target) {
        if (source.getId() == target.getId()) {
            throw new SameSourceTargetUserException();
        }
    }
}
