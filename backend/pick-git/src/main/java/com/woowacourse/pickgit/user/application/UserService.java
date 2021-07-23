package com.woowacourse.pickgit.user.application;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.user.application.dto.AuthUserResponseDto;
import com.woowacourse.pickgit.user.application.dto.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.UserProfileResponseDto;
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
    public UserProfileResponseDto getMyUserProfile(
        AuthUserResponseDto authUserResponseDto) {
        User user = findUserByName(authUserResponseDto.getGithubName());

        return new UserProfileResponseDto(
            user.getName(), user.getImage(), user.getDescription(),
            user.getFollowerCount(), user.getFollowingCount(), user.getPostCount(),
            user.getGithubUrl(), user.getCompany(), user.getLocation(),
            user.getWebsite(), user.getTwitter(), null
        );
    }

    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserProfile(AppUser appUser, String targetUsername) {
        User targetUser = findUserByName(targetUsername);

        if (appUser.isGuest()) {
            return new UserProfileResponseDto(
                targetUser.getName(), targetUser.getImage(), targetUser.getDescription(),
                targetUser.getFollowerCount(), targetUser.getFollowingCount(), targetUser.getPostCount(),
                targetUser.getGithubUrl(), targetUser.getCompany(), targetUser.getLocation(),
                targetUser.getWebsite(), targetUser.getTwitter(), null
            );
        }

        User sourceUser = findUserByName(appUser.getUsername());

        return new UserProfileResponseDto(
            targetUser.getName(), targetUser.getImage(), targetUser.getDescription(),
            targetUser.getFollowerCount(), targetUser.getFollowingCount(), targetUser.getPostCount(),
            targetUser.getGithubUrl(), targetUser.getCompany(), targetUser.getLocation(),
            targetUser.getWebsite(), targetUser.getTwitter(), sourceUser.isFollowing(targetUser)
        );
    }

    public FollowResponseDto followUser(AuthUserResponseDto authUserResponseDto,
        String targetUsername) {
        User source = findUserByName(authUserResponseDto.getGithubName());
        User target = findUserByName(targetUsername);

        validateDifferentSourceTarget(source, target);
        source.follow(target);

        return new FollowResponseDto(target.getFollowerCount(), true);
    }

    public FollowResponseDto unfollowUser(AuthUserResponseDto authUserResponseDto,
        String targetUsername) {
        User source = findUserByName(authUserResponseDto.getGithubName());
        User target = findUserByName(targetUsername);

        validateDifferentSourceTarget(source, target);
        source.unfollow(target);

        return new FollowResponseDto(target.getFollowerCount(), false);
    }

    private User findUserByName(String githubName) {
        return userRepository
            .findByBasicProfile_Name(githubName)
            .orElseThrow(InvalidUserException::new);
    }

    private void validateDifferentSourceTarget(User source, User target) {
        if (source.getId().equals(target.getId())) {
            throw new SameSourceTargetUserException();
        }
    }
}
