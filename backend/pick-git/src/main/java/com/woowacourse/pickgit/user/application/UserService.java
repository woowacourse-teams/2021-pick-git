package com.woowacourse.pickgit.user.application;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.exception.user.SameSourceTargetUserException;
import com.woowacourse.pickgit.user.application.dto.AuthUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.UserProfileResponseDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
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
    public UserProfileResponseDto getMyUserProfile(AuthUserRequestDto requestDto) {
        User user = findUserByName(requestDto.getGithubName());

        return new UserProfileResponseDto(
            user.getName(), user.getImage(), user.getDescription(),
            user.getFollowerCount(), user.getFollowingCount(), user.getPostCount(),
            user.getGithubUrl(), user.getCompany(), user.getLocation(),
            user.getWebsite(), user.getTwitter(), false);
    }

    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserProfile(AppUser user, String targetName) {
        User target = findUserByName(targetName);

        if (user.isGuest()) {
            return new UserProfileResponseDto(
                target.getName(), target.getImage(), target.getDescription(),
                target.getFollowerCount(), target.getFollowingCount(),
                target.getPostCount(),
                target.getGithubUrl(), target.getCompany(), target.getLocation(),
                target.getWebsite(), target.getTwitter(), null);
        }

        User source = findUserByName(user.getUsername());

        return new UserProfileResponseDto(
            target.getName(), target.getImage(), target.getDescription(),
            target.getFollowerCount(), target.getFollowingCount(),
            target.getPostCount(),
            target.getGithubUrl(), target.getCompany(), target.getLocation(),
            target.getWebsite(), target.getTwitter(), source.isFollowing(target)
        );
    }

    public FollowResponseDto followUser(AuthUserRequestDto requestDto, String targetName) {
        User source = findUserByName(requestDto.getGithubName());
        User target = findUserByName(targetName);

        validateDifferentSourceTarget(source, target);
        source.follow(target);

        return new FollowResponseDto(target.getFollowerCount(), true);
    }

    public FollowResponseDto unfollowUser(AuthUserRequestDto requestDto, String targetName) {
        User source = findUserByName(requestDto.getGithubName());
        User target = findUserByName(targetName);

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
