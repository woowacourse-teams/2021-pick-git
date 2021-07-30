package com.woowacourse.pickgit.user.presentation.dto.response;

import lombok.Builder;

@Builder
public class ContributionResponse {

    private int starsCount;
    private int commitsCount;
    private int prsCount;
    private int issuesCount;
    private int reposCount;

    private ContributionResponse() {
    }

    public ContributionResponse(
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
