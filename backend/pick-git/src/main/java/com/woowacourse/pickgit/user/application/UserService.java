package com.woowacourse.pickgit.user.application;

import com.woowacourse.pickgit.user.application.dto.AuthUserServiceDto;
import com.woowacourse.pickgit.user.application.dto.FollowServiceDto;
import com.woowacourse.pickgit.user.application.dto.UserProfileServiceDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.exception.InvalidUserException;
import com.woowacourse.pickgit.user.exception.SameSourceTargetUserException;
import javax.security.sasl.SaslException;
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
    public UserProfileServiceDto getAuthUserProfile(AuthUserServiceDto authUserServiceDto) {
        return getUserProfile(authUserServiceDto.getGithubName());
    }

    @Transactional(readOnly = true)
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
