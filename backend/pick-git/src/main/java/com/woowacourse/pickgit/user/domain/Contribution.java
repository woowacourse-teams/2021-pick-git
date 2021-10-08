package com.woowacourse.pickgit.user.domain;

import lombok.Builder;

@Builder
public class Contribution {

    private int starsCount;
    private int commitsCount;
    private int prsCount;
    private int issuesCount;
    private int reposCount;

    private Contribution() {
    }

    public Contribution(
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
