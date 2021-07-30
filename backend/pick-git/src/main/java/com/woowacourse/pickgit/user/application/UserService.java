package com.woowacourse.pickgit.user.application;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.exception.user.SameSourceTargetUserException;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.domain.Contribution;
import com.woowacourse.pickgit.user.domain.PlatformContributionCalculator;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.application.dto.request.UserSearchRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PlatformContributionCalculator platformContributionCalculator;

    public UserService(
        UserRepository userRepository,
        PlatformContributionCalculator platformContributionCalculator
    ) {
        this.userRepository = userRepository;
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
        List<User> users = userRepository.findAllByUsername(request.getKeyword(), pageable);

        if (appUser.isGuest()) {
            return users.stream()
                .map(user -> new UserSearchResponseDto(user.getImage(), user.getName(), null))
                .collect(toList());
        }

        User loginUser = findUserByName(appUser.getUsername());

        return users
            .stream()
            .filter(user -> !user.equals(loginUser))
            .map(user -> new UserSearchResponseDto(user.getImage(), user.getName(), loginUser.isFollowing(user)))
            .collect(toList());
    }
}
