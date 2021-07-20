package com.woowacourse.pickgit.authentication.domain.user;

import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;

public class GuestUser extends AppUser {

    private static final String DUMMY_USERNAME = "anonymous";
    private static final String DUMMY_ACCESSTOKEN = "anonymous";

    public GuestUser() {
        super(DUMMY_USERNAME, DUMMY_ACCESSTOKEN);
    }

    @Override
    public boolean isGuest() {
        return true;
    }

    @Override
    public String getUsername() {
        throw new UnauthorizedException();
    }

    @Override
    public String getAccessToken() {
        throw new UnauthorizedException();
    }
}
