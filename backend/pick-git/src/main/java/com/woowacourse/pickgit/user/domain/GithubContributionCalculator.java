package com.woowacourse.pickgit.user.domain;

import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.domain.dto.CountResponseDto;
import com.woowacourse.pickgit.user.domain.dto.StarsResponseDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GithubContributionCalculator implements PlatformContributionCalculator {

    private final PlatformContributionExtractor platformContributionExtractor;

    public GithubContributionCalculator(
        PlatformContributionExtractor platformContributionExtractor
    ) {
        this.platformContributionExtractor = platformContributionExtractor;
    }

    @Override
    public ContributionResponseDto calculate(String username) {
        return ContributionResponseDto.builder()
            .starsCount(calculateStars(username))
            .commitsCount(calculateCommits(username))
            .prsCount(calculatePRs(username))
            .issuesCount(calculateIssues(username))
            .reposCount(calculateRepos(username))
            .build();
    }

    private int calculateStars(String username) {
        List<StarsResponseDto> responsesDto = platformContributionExtractor
            .extractStars(username);

        return responsesDto.stream()
            .mapToInt(StarsResponseDto::getStars)
            .sum();
    }

    private int calculateCommits(String username) {
        CountResponseDto responseDto = platformContributionExtractor
            .extractCount("/commits?q=committer:%s", username);

        return responseDto.getCount();
    }

    private int calculatePRs(String username) {
        CountResponseDto responseDto = platformContributionExtractor
            .extractCount("/issues?q=author:%s type:pr", username);

        return responseDto.getCount();
    }

    private int calculateIssues(String username) {
        CountResponseDto responseDto = platformContributionExtractor
            .extractCount("/issues?q=author:%s type:issue", username);

        return responseDto.getCount();
    }

    private int calculateRepos(String username) {
        CountResponseDto responseDto = platformContributionExtractor
            .extractCount("/repositories?q=user:%s is:public", username);

        return responseDto.getCount();
    }
}
