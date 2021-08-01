package com.woowacourse.pickgit.user.application;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.post.domain.repository.PickGitStorage;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.ProfileEditRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.UserSearchRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.ProfileEditResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import com.woowacourse.pickgit.user.domain.Contribution;
import com.woowacourse.pickgit.user.domain.PlatformContributionCalculator;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private final PickGitStorage pickGitStorage;
    private final PlatformContributionCalculator platformContributionCalculator;

    public UserService(
        UserRepository userRepository,
        PickGitStorage pickGitStorage,
        PlatformContributionCalculator platformContributionCalculator
    ) {
        this.userRepository = userRepository;
        this.pickGitStorage = pickGitStorage;
        this.platformContributionCalculator = platformContributionCalculator;
    }

    @Transactional(readOnly = true)
    public UserProfileResponseDto getMyUserProfile(AuthUserRequestDto requestDto) {
        validateIsGuest(requestDto);
        User user = findUserByName(requestDto.getUsername());
        return generateUserProfileResponse(user, null);
    }

    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserProfile(AuthUserRequestDto requestDto, String targetName) {
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
        AuthUserRequestDto authUserRequestDto,
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
            Path tempFile = Files.createTempFile(null, null);
            Files.write(tempFile, multipartFile.getBytes());

            return tempFile.toFile();
        } catch (IOException ioException) {
            throw new PlatformHttpErrorException();
        }
    }

    private String saveImageAndGetUrl(File file, String username) {
        return pickGitStorage
            .store(file, username)
            .orElseThrow(PlatformHttpErrorException::new);
    }

    public FollowResponseDto followUser(AuthUserRequestDto requestDto, String targetName) {
        validateIsGuest(requestDto);
        User source = findUserByName(requestDto.getUsername());
        User target = findUserByName(targetName);
        source.follow(target);
        return generateFollowResponse(target, true);
    }

    public FollowResponseDto unfollowUser(AuthUserRequestDto requestDto, String targetName) {
        validateIsGuest(requestDto);
        User source = findUserByName(requestDto.getUsername());
        User target = findUserByName(targetName);
        source.unfollow(target);
        return generateFollowResponse(target, false);
    }

    private FollowResponseDto generateFollowResponse(User target, boolean isFollowing) {
        return FollowResponseDto.builder()
            .followerCount(target.getFollowerCount())
            .isFollowing(isFollowing)
            .build();
    }

    public ContributionResponseDto calculateContributions(String username) {
        User user = findUserByName(username);

        Contribution contribution = platformContributionCalculator.calculate(user.getName());

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
        AuthUserRequestDto authUserRequestDto,
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

    private List<UserSearchResponseDto> convertToUserSearchResponseDtoWithoutFollowing(
        List<User> users
    ) {
        return users.stream()
            .map(user -> new UserSearchResponseDto(
                user.getImage(),
                user.getName(),
                null))
            .collect(toList());
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

    private void validateIsGuest(AuthUserRequestDto requestDto) {
        if (requestDto.isGuest()) {
            throw new UnauthorizedException();
        }
    }

    private User findUserByName(String githubName) {
        return userRepository.findByBasicProfile_Name(githubName)
            .orElseThrow(InvalidUserException::new);
    }
}
