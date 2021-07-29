package com.woowacourse.pickgit.user.application.dto.response;

import lombok.Builder;

@Builder
public class ContributionResponseDto {

    private int starsCount;
    private int commitsCount;
    private int prsCount;
    private int issuesCount;
    private int reposCount;

    private ContributionResponseDto() {
    }

    public ContributionResponseDto(
        int starsCount,
        int commitsCount,
        int prsCount,
        int issuesCount,
        int reposCount
    ) {
        this.starsCount = starsCount;
        this.commitsCount = commitsCount;
        this.prsCount = prsCount;
        this.issuesCount = issuesCount;
        this.reposCount = reposCount;
    }

    public int getStarsCount() {
        return starsCount;
    }

    public int getCommitsCount() {
        return commitsCount;
    }

    public int getPrsCount() {
        return prsCount;
    }

    public int getIssuesCount() {
        return issuesCount;
    }

    public int getReposCount() {
        return reposCount;
    }
}
