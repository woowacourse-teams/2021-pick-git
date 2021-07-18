package com.woowacourse.pickgit.user;

import com.woowacourse.pickgit.user.application.dto.UserProfileServiceDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import com.woowacourse.pickgit.user.presentation.dto.UserProfileResponse;

public class UserFactory {

    private static final Long ID_SOURCE = 1L;
    private static final Long ID_TARGET = 2L;
    private static final String NAME_SOURCE = "yjksw";
    private static final String NAME_TARGET = "pickgit";
    private static final String IMAGE = "http://img.com";
    private static final String DESCRIPTION = "The Best";
    private static final String GITHUB_URL = "https://github.com/yjksw";
    private static final String COMPANY = "woowacourse";
    private static final String LOCATION = "Seoul";
    private static final String WEBSITE = "www.pick-git.com";
    private static final String TWITTER = "pick-git twitter";

    private BasicProfile basicProfileSource =
        new BasicProfile(NAME_SOURCE, IMAGE, DESCRIPTION);
    private BasicProfile basicProfileTarget =
        new BasicProfile(NAME_TARGET, IMAGE, DESCRIPTION);
    private GithubProfile githubProfile_source =
        new GithubProfile(GITHUB_URL, COMPANY, LOCATION, WEBSITE, TWITTER);
    private GithubProfile githubProfile_target =
        new GithubProfile(GITHUB_URL, COMPANY, LOCATION, WEBSITE, TWITTER);

    public User user() {
        return new User(ID_SOURCE, basicProfileSource, githubProfile_source);
    }

    public User anotherUser() {
        return new User(ID_TARGET, basicProfileTarget, githubProfile_target);
    }

    public UserProfileServiceDto mockLoginUserProfileServiceDto() {
        return new UserProfileServiceDto(
            NAME_SOURCE, IMAGE, DESCRIPTION, 0, 11,
            1, GITHUB_URL, COMPANY, LOCATION, WEBSITE, TWITTER, false);
    }
    public UserProfileServiceDto mockUnLoginUserProfileServiceDto() {
        return new UserProfileServiceDto(
            NAME_SOURCE, IMAGE, DESCRIPTION, 0, 11,
            1, GITHUB_URL, COMPANY, LOCATION, WEBSITE, TWITTER, null);
    }

}
