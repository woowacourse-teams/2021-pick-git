package com.woowacourse.pickgit.user.application;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.exception.user.SameSourceTargetUserException;
import com.woowacourse.pickgit.post.domain.PickGitStorage;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.request.ProfileEditRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.ProfileEditResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.domain.Contribution;
import com.woowacourse.pickgit.user.domain.PlatformContributionCalculator;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.application.dto.request.UserSearchRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
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
        User user = findUserByName(requestDto.getGithubName());

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
            .following(false)
            .build();
    }

    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserProfile(AppUser user, String targetName) {
        User target = findUserByName(targetName);

        if (user.isGuest()) {
            return UserProfileResponseDto.builder()
                .name(target.getName())
                .imageUrl(target.getImage())
                .description(target.getDescription())
                .followerCount(target.getFollowerCount())
                .followingCount(target.getFollowingCount())
                .postCount(target.getPostCount())
                .githubUrl(target.getGithubUrl())
                .company(target.getCompany())
                .location(target.getLocation())
                .website(target.getWebsite())
                .twitter(target.getTwitter())
                .following(null)
                .build();
        }

        User source = findUserByName(user.getUsername());

        return UserProfileResponseDto.builder()
            .name(target.getName())
            .imageUrl(target.getImage())
            .description(target.getDescription())
            .followerCount(target.getFollowerCount())
            .followingCount(target.getFollowingCount())
            .postCount(target.getPostCount())
            .githubUrl(target.getGithubUrl())
            .company(target.getCompany())
            .location(target.getLocation())
            .website(target.getWebsite())
            .twitter(target.getTwitter())
            .following(source.isFollowing(target))
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

    public ProfileEditResponseDto editProfile(AppUser appUser, ProfileEditRequestDto requestDto) {
        User user = findUserByName(appUser.getUsername());

        String userImageUrl = user.getImage();
        if (doesContainProfileImage(requestDto.getImage())) {
            userImageUrl = saveImageAndGetUrl(requestDto.getImage(), user.getName());
            user.updateProfileImage(userImageUrl);
        }

        user.updateDescription(requestDto.getDecription());

        return new ProfileEditResponseDto(userImageUrl, requestDto.getDecription());
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
        User source = findUserByName(requestDto.getGithubName());
        User target = findUserByName(targetName);

        validateDifferentSourceTarget(source, target);
        source.follow(target);

        return FollowResponseDto.builder()
            .followerCount(target.getFollowerCount())
            .isFollowing(true)
            .build();
    }

    public FollowResponseDto unfollowUser(AuthUserRequestDto requestDto, String targetName) {
        User source = findUserByName(requestDto.getGithubName());
        User target = findUserByName(targetName);

        validateDifferentSourceTarget(source, target);
        source.unfollow(target);

        return FollowResponseDto.builder()
            .followerCount(target.getFollowerCount())
            .isFollowing(false)
            .build();
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

    @Transactional(readOnly = true)
    public List<UserSearchResponseDto> searchUser(AppUser appUser, UserSearchRequestDto request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getLimit());
        List<User> users = userRepository.searchByUsernameLike(request.getKeyword(), pageable);

        if (appUser.isGuest()) {
            return convertToUserSearchResponseDtoWithoutFollowing(users);
        }

        User loginUser = findUserByName(appUser.getUsername());

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
}
