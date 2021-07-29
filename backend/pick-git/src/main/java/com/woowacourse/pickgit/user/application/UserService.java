package com.woowacourse.pickgit.user.application;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.exception.user.SameSourceTargetUserException;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.domain.PlatformContributionExtractor;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.domain.dto.CountResponseDto;
import com.woowacourse.pickgit.user.domain.dto.StarResponseDto;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PlatformContributionExtractor platformContributionExtractor;

    public UserService(
        UserRepository userRepository,
        PlatformContributionExtractor platformContributionExtractor
    ) {
        this.userRepository = userRepository;
        this.platformContributionExtractor = platformContributionExtractor;
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

        return ContributionResponseDto.builder()
            .starsCount(calculateStars(user.getName()))
            .commitsCount(calculateCommits(user.getName()))
            .prsCount(calculatePRs(user.getName()))
            .issuesCount(calculateIssues(user.getName()))
            .reposCount(calculateRepos(user.getName()))
            .build();
    }

    private int calculateStars(String username) {
        List<StarResponseDto> responseDtos = platformContributionExtractor.extractStars(username);

        return responseDtos.stream()
            .mapToInt(StarResponseDto::getStars)
            .sum();
    }

    private int calculateCommits(String username) {
        CountResponseDto responseDto =
            platformContributionExtractor.extractCount("/commits?q=committer:%s", username);

        return responseDto.getCount();
    }

    private int calculatePRs(String username) {
        CountResponseDto responseDto =
            platformContributionExtractor.extractCount("/issues?q=author:%s type:pr", username);

        return responseDto.getCount();
    }

    private int calculateIssues(String username) {
        CountResponseDto responseDto =
            platformContributionExtractor.extractCount("/issues?q=author:%s type:issue", username);

        return responseDto.getCount();
    }

    private int calculateRepos(String username) {
        CountResponseDto responseDto =
            platformContributionExtractor.extractCount("/repositories?q=user:%s is:public", username);

        return responseDto.getCount();
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
}
