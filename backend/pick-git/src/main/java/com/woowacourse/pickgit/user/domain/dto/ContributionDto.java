package com.woowacourse.pickgit.user.domain.dto;

import lombok.Builder;

@Builder
public class ContributionDto {

    private int starsCount;
    private int commitsCount;
    private int prsCount;
    private int issuesCount;
    private int reposCount;

    private ContributionDto() {
    }

    public ContributionDto(
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
