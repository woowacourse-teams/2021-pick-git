package com.woowacourse.pickgit.user.application;

import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.user.application.dto.UserDtoAssembler;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserForUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.FollowRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.ProfileImageEditRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.ProfileImageEditResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.contribution.Contribution;
import com.woowacourse.pickgit.user.domain.contribution.PlatformContributionCalculator;
import com.woowacourse.pickgit.user.domain.follow.PlatformFollowingRequester;
import com.woowacourse.pickgit.user.domain.profile.PickGitProfileStorage;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import com.woowacourse.pickgit.user.domain.search.UserSearchEngine;
import com.woowacourse.pickgit.user.presentation.dto.request.ContributionRequestDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserSearchEngine userSearchEngine;
    private final PickGitProfileStorage pickGitProfileStorage;
    private final PlatformContributionCalculator platformContributionCalculator;
    private final PlatformFollowingRequester platformFollowingRequester;

    public UserProfileResponseDto getMyUserProfile(AuthUserForUserRequestDto requestDto) {
        User user = findUserByName(requestDto.getUsername());
        return UserDtoAssembler.userProfileResponseDto(user, null);
    }

    public UserProfileResponseDto getUserProfile(
        AuthUserForUserRequestDto requestDto,
        String targetName
    ) {
        User target = findUserByName(targetName);
        if (requestDto.isGuest()) {
            return UserDtoAssembler.userProfileResponseDto(target, null);
        }

        User source = findUserByName(requestDto.getUsername());

        return UserDtoAssembler.userProfileResponseDto(target, source.isFollowing(target));
    }

    @Transactional
    public ProfileImageEditResponseDto editProfileImage(
        AuthUserForUserRequestDto authUserRequestDto,
        ProfileImageEditRequestDto profileImageEditRequestDto
    ) {
        User user = findUserByName(authUserRequestDto.getUsername());

        String userImageUrl = pickGitProfileStorage.storeByteFile(
            profileImageEditRequestDto.getImage(),
            user.getName()
        );

        user.updateProfileImage(userImageUrl);

        return new ProfileImageEditResponseDto(userImageUrl);
    }

    @Transactional
    public String editProfileDescription(
        AuthUserForUserRequestDto authUserRequestDto,
        String description
    ) {
        User user = findUserByName(authUserRequestDto.getUsername());
        user.updateDescription(description);

        return description;
    }

    @Transactional
    public FollowResponseDto followUser(FollowRequestDto requestDto) {
        AuthUserForUserRequestDto authUserRequestDto = requestDto.getAuthUserRequestDto();
        User source = findUserByName(authUserRequestDto.getUsername());
        User target = findUserByName(requestDto.getTargetName());

        source.follow(target);
        followInPlatform(requestDto);

        return UserDtoAssembler.followResponseDto(target, true);
    }

    private void followInPlatform(FollowRequestDto requestDto) {
        if (requestDto.isGithubFollowing()) {
            platformFollowingRequester.follow(
                requestDto.getTargetName(),
                requestDto.getAccessToken()
            );
        }
    }

    @Transactional
    public FollowResponseDto unfollowUser(FollowRequestDto requestDto) {
        AuthUserForUserRequestDto authUserRequestDto = requestDto.getAuthUserRequestDto();
        User source = findUserByName(authUserRequestDto.getUsername());
        User target = findUserByName(requestDto.getTargetName());

        source.unfollow(target);
        unfollowInPlatform(requestDto);
        return UserDtoAssembler.followResponseDto(target, false);
    }

    private void unfollowInPlatform(FollowRequestDto requestDto) {
        if (requestDto.isGithubFollowing()) {
            platformFollowingRequester.unfollow(
                requestDto.getTargetName(),
                requestDto.getAccessToken()
            );
        }
    }

    public ContributionResponseDto calculateContributions(ContributionRequestDto requestDto) {
        User user = findUserByName(requestDto.getUsername());
        Contribution contribution =
            platformContributionCalculator.calculate(requestDto.getAccessToken(), user.getName());

        return UserDtoAssembler.contributionResponseDto(contribution);
    }

    public List<UserSearchResponseDto> searchUser(
        AuthUserForUserRequestDto authUserRequestDto,
        String keyword,
        Pageable pageable
    ) {
        List<User> users = userSearchEngine.searchByUsernameLike(keyword, pageable);

        if (authUserRequestDto.isGuest()) {
            return UserDtoAssembler.userSearchResponseDto(users);
        }

        User loginUser = findUserByName(authUserRequestDto.getUsername());
        return UserDtoAssembler.UserSearchResponseDto(loginUser, users);
    }

    public List<UserSearchResponseDto> searchFollowings(
        AuthUserForUserRequestDto authUserRequestDto,
        String username,
        Pageable pageable
    ) {
        User target = findUserByName(username);
        List<User> followings = userRepository.searchFollowingsOf(target, pageable);

        return generateUserSearchResponseDtosByLoginExistence(authUserRequestDto, followings);
    }

    public List<UserSearchResponseDto> searchFollowers(
        AuthUserForUserRequestDto authUserRequestDto,
        String username,
        Pageable pageable
    ) {
        User target = findUserByName(username);
        List<User> followers = userRepository.searchFollowersOf(target, pageable);

        return generateUserSearchResponseDtosByLoginExistence(authUserRequestDto, followers);
    }

    private List<UserSearchResponseDto> generateUserSearchResponseDtosByLoginExistence(
        AuthUserForUserRequestDto authUserRequestDto,
        List<User> users
    ) {
        if (authUserRequestDto.isGuest()) {
            return UserDtoAssembler.userSearchResponseDto(users);
        }

        User loginUser = findUserByName(authUserRequestDto.getUsername());
        return UserDtoAssembler.userSearchResponseDto(loginUser, users);
    }

    private User findUserByName(String githubName) {
        return userRepository.findByBasicProfile_Name(githubName)
            .orElseThrow(InvalidUserException::new);
    }
}
