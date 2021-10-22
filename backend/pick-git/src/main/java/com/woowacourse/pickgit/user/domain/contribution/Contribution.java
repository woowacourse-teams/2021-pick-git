package com.woowacourse.pickgit.user.domain.contribution;

import java.util.Map;

public class Contribution {

    private final Map<ContributionCategory, Integer> contribution;

    public Contribution(Map<ContributionCategory, Integer> contribution) {
        this.contribution = contribution;
    }

    public void put(ContributionCategory key, Integer value) {
        contribution.put(key, value);
    }

    public int getStarsCount() {
        return contribution.get(ContributionCategory.STAR);
    }

    public int getCommitsCount() {
        return contribution.get(ContributionCategory.COMMIT);
    }

    public int getPrsCount() {
        return contribution.get(ContributionCategory.PR);
    }

    public int getIssuesCount() {
        return contribution.get(ContributionCategory.ISSUE);
    }

    public int getReposCount() {
        return contribution.get(ContributionCategory.REPO);
    }
}
