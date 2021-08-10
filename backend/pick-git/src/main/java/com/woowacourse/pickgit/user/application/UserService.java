package com.woowacourse.pickgit.user.application;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserForUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.FollowRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.FollowSearchRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.ProfileEditRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.ProfileImageEditRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.UserSearchRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.ProfileEditResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.ProfileImageEditResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import com.woowacourse.pickgit.user.domain.Contribution;
import com.woowacourse.pickgit.user.domain.PlatformContributionCalculator;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.domain.follow.PlatformFollowingRequester;
import com.woowacourse.pickgit.user.domain.profile.PickGitProfileStorage;
import com.woowacourse.pickgit.user.presentation.dto.request.ContributionRequestDto;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PickGitProfileStorage pickGitProfileStorage;
    private final PlatformContributionCalculator platformContributionCalculator;
    private final PlatformFollowingRequester platformFollowingRequester;

    public UserService(
        UserRepository userRepository,
        PickGitProfileStorage pickGitProfileStorage,
        PlatformContributionCalculator platformContributionCalculator,
        PlatformFollowingRequester platformFollowingRequester
    ) {
        this.userRepository = userRepository;
        this.pickGitProfileStorage = pickGitProfileStorage;
        this.platformContributionCalculator = platformContributionCalculator;
        this.platformFollowingRequester = platformFollowingRequester;
    }

    @Transactional(readOnly = true)
    public UserProfileResponseDto getMyUserProfile(AuthUserForUserRequestDto requestDto) {
        validateIsGuest(requestDto);
        User user = findUserByName(requestDto.getUsername());
        return generateUserProfileResponse(user, null);
    }

    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserProfile(AuthUserForUserRequestDto requestDto, String targetName) {
        User target = findUserByName(targetName);
        if (requestDto.isGuest()) {
            return generateUserProfileResponse(target, null);
        }
        User source = findUserByName(requestDto.getUsername());
        return generateUserProfileResponse(target, source.isFollowing(target));
    }

    private UserProfileResponseDto generateUserProfileResponse(User user, Boolean following) {
        return UserProfileResponseDto.builder()
            .name(user.getName())
            .imageUrl(user.getImage())
            .description(user.getDescription())
            .followerCount(user.getFollowerCount())
            .followingCount(user.getFollowingCount())
            .postCount(user.getPostCount())
            .githubUrl(user.getGithubUrl())
            .company(user.getCompany())
            .location(user.getLocation())
            .website(user.getWebsite())
            .twitter(user.getTwitter())
            .following(following)
            .build();
    }

    public ProfileEditResponseDto editProfile(
        AuthUserForUserRequestDto authUserRequestDto,
        ProfileEditRequestDto profileEditRequestDto
    ) {
        validateIsGuest(authUserRequestDto);
        User user = findUserByName(authUserRequestDto.getUsername());

        String userImageUrl = user.getImage();
        if (doesContainProfileImage(profileEditRequestDto.getImage())) {
            userImageUrl = saveImageAndGetUrl(profileEditRequestDto.getImage(), user.getName());
            user.updateProfileImage(userImageUrl);
        }

        user.updateDescription(profileEditRequestDto.getDecription());

        return new ProfileEditResponseDto(userImageUrl, profileEditRequestDto.getDecription());
    }

    private boolean doesContainProfileImage(MultipartFile image) {
        if (Objects.isNull(image)) {
            return false;
        }
        return !Objects.requireNonNull(image.getOriginalFilename()).isBlank();
    }

    private String saveImageAndGetUrl(MultipartFile image, String username) {
        File file = fileFrom(image);

        return saveImageAndGetUrl(file, username);
    }

    private File fileFrom(MultipartFile image) {
        try {
            return image.getResource().getFile();
        } catch (IOException e) {
            return tryCreateTempFile(image);
        }
    }

    private File tryCreateTempFile(MultipartFile multipartFile) {
        try {
            File tempFile = File.createTempFile("temp", null, null);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(multipartFile.getBytes());
                return tempFile;
            }
        } catch (IOException e) {
            throw new PlatformHttpErrorException();
        }
    }

    private String saveImageAndGetUrl(File file, String username) {
        return pickGitProfileStorage
            .store(file, username)
            .orElseThrow(PlatformHttpErrorException::new);
    }

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

    public String editProfileDescription(
        AuthUserForUserRequestDto authUserRequestDto,
        String description
    ) {
        User user = findUserByName(authUserRequestDto.getUsername());

        user.updateDescription(description);
        return description;
    }

    public FollowResponseDto followUser(FollowRequestDto requestDto) {
        AuthUserForUserRequestDto authUserRequestDto = requestDto.getAuthUserRequestDto();
        validateIsGuest(authUserRequestDto);
        User source = findUserByName(authUserRequestDto.getUsername());
        User target = findUserByName(requestDto.getTargetName());

        source.follow(target);
        followInPlatform(requestDto);
        return generateFollowResponse(target, true);
    }

    private void followInPlatform(FollowRequestDto requestDto) {
        if (requestDto.isGithubFollowing()) {
            platformFollowingRequester.follow(
                requestDto.getTargetName(),
                requestDto.getAccessToken()
            );
        }
    }

    public FollowResponseDto unfollowUser(FollowRequestDto requestDto) {
        AuthUserForUserRequestDto authUserRequestDto = requestDto.getAuthUserRequestDto();
        validateIsGuest(authUserRequestDto);
        User source = findUserByName(authUserRequestDto.getUsername());
        User target = findUserByName(requestDto.getTargetName());

        source.unfollow(target);
        unfollowInPlatform(requestDto);
        return generateFollowResponse(target, false);
    }

    private void unfollowInPlatform(FollowRequestDto requestDto) {
        if (requestDto.isGithubFollowing()) {
            platformFollowingRequester.unfollow(
                requestDto.getTargetName(),
                requestDto.getAccessToken()
            );
        }
    }

    private void validateIsGuest(AuthUserForUserRequestDto requestDto) {
        if (requestDto.isGuest()) {
            throw new UnauthorizedException();
        }
    }

    private FollowResponseDto generateFollowResponse(User target, boolean isFollowing) {
        return FollowResponseDto.builder()
            .followerCount(target.getFollowerCount())
            .isFollowing(isFollowing)
            .build();
    }

    public ContributionResponseDto calculateContributions(ContributionRequestDto requestDto) {
        User user = findUserByName(requestDto.getUsername());

        Contribution contribution = platformContributionCalculator
            .calculate(requestDto.getAccessToken(), user.getName());

        return ContributionResponseDto.builder()
            .starsCount(contribution.getStarsCount())
            .commitsCount(contribution.getCommitsCount())
            .prsCount(contribution.getPrsCount())
            .issuesCount(contribution.getIssuesCount())
            .reposCount(contribution.getReposCount())
            .build();
    }

    @Transactional(readOnly = true)
    public List<UserSearchResponseDto> searchUser(
        AuthUserForUserRequestDto authUserRequestDto,
        UserSearchRequestDto userSearchRequestDto
    ) {
        Pageable pageable = PageRequest.of(
            userSearchRequestDto.getPage(),
            userSearchRequestDto.getLimit()
        );
        List<User> users = userRepository.searchByUsernameLike(
            userSearchRequestDto.getKeyword(),
            pageable
        );

        if (authUserRequestDto.isGuest()) {
            return convertToUserSearchResponseDtoWithoutFollowing(users);
        }

        User loginUser = findUserByName(authUserRequestDto.getUsername());

        return convertToUserSearchResponseDtoWithFollowing(loginUser, users);
    }

    @Transactional(readOnly = true)
    public List<UserSearchResponseDto> searchFollowings(
        AuthUserForUserRequestDto authUserRequestDto,
        FollowSearchRequestDto followSearchRequestDto
    ) {
        User target = findUserByName(followSearchRequestDto.getUsername());
        Pageable pageable = PageRequest.of(
            Math.toIntExact(followSearchRequestDto.getPage()),
            Math.toIntExact(followSearchRequestDto.getLimit())
        );
        List<User> followings = userRepository.searchFollowingsOf(target, pageable);

        if (authUserRequestDto.isGuest()) {
            return convertToUserSearchResponseDtoWithoutFollowing(followings);
        }

        User loginUser = findUserByName(authUserRequestDto.getUsername());
        return convertToUserSearchResponseDtoWithFollowingAndIncludingMe(loginUser, followings);
    }

    @Transactional(readOnly = true)
    public List<UserSearchResponseDto> searchFollowers(
        AuthUserForUserRequestDto authUserRequestDto,
        FollowSearchRequestDto followSearchRequestDto
    ) {
        User target = findUserByName(followSearchRequestDto.getUsername());
        Pageable pageable = PageRequest.of(
            Math.toIntExact(followSearchRequestDto.getPage()),
            Math.toIntExact(followSearchRequestDto.getLimit())
        );
        List<User> followers = userRepository.searchFollowersOf(target, pageable);

        if (authUserRequestDto.isGuest()) {
            return convertToUserSearchResponseDtoWithoutFollowing(followers);
        }

        User loginUser = findUserByName(authUserRequestDto.getUsername());
        return convertToUserSearchResponseDtoWithFollowingAndIncludingMe(loginUser, followers);
    }

    private List<UserSearchResponseDto> convertToUserSearchResponseDtoWithFollowingAndIncludingMe(
        User loginUser,
        List<User> followings
    ) {
        return followings.stream()
            .map(followUser -> convert(loginUser, followUser))
            .collect(toList());
    }

    private UserSearchResponseDto convert(User loginUser, User followUser) {
        if (loginUser.equals(followUser)) {
            return new UserSearchResponseDto(loginUser.getImage(), loginUser.getName(), null);
        }
        return new UserSearchResponseDto(
            followUser.getImage(),
            followUser.getName(),
            loginUser.isFollowing(followUser)
        );
    }

    private List<UserSearchResponseDto> convertToUserSearchResponseDtoWithoutFollowing(
        List<User> users
    ) {
        return users.stream()
            .map(user -> new UserSearchResponseDto(user.getImage(), user.getName(), null))
            .collect(toList());
    }

    private User findUserByName(String githubName) {
        return userRepository.findByBasicProfile_Name(githubName)
            .orElseThrow(InvalidUserException::new);
    }

    private List<UserSearchResponseDto> convertToUserSearchResponseDtoWithFollowing(
        User loginUser,
        List<User> users
    ) {
        return users
            .stream()
            .filter(isLoginUser(loginUser))
            .map(user -> new UserSearchResponseDto(
                user.getImage(),
                user.getName(),
                loginUser.isFollowing(user)))
            .collect(toList());
    }

    private Predicate<User> isLoginUser(User loginUser) {
        return user -> !user.equals(loginUser);
    }
}
