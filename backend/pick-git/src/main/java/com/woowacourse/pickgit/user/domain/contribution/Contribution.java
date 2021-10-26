package com.woowacourse.pickgit.user.domain.contribution;

import java.util.Map;

public class Contribution {

    private final Map<ContributionCategory, Integer> contributions;

    public Contribution(Map<ContributionCategory, Integer> contributions) {
        this.contributions = contributions;
    }

    public void put(ContributionCategory key, Integer value) {
        contributions.put(key, value);
    }

    public int getStarsCount() {
        return contributions.get(ContributionCategory.STAR);
    }

    public int getCommitsCount() {
        return contributions.get(ContributionCategory.COMMIT);
    }

    public int getPrsCount() {
        return contributions.get(ContributionCategory.PR);
    }

    public int getIssuesCount() {
        return contributions.get(ContributionCategory.ISSUE);
    }

    public int getReposCount() {
        return contributions.get(ContributionCategory.REPO);
    }
}
