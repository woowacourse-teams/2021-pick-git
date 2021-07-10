package com.woowacourse.pickgit.authentication.domain.user;

public class Anonymous extends RequestUser{
    private static final String DUMMY_USERNAME = "anonymous";
    private static final String DUMMY_ACCESSTOKEN = "anonymous";

    public Anonymous() {
        super(DUMMY_USERNAME, DUMMY_ACCESSTOKEN);
    }

    @Override
    public boolean isAnonymous() {
        return true;
    }

    @Override
    public String getUsername() {
        throw new IllegalArgumentException("에러!");
    }

    @Override
    public String getAccessToken() {
        throw new IllegalArgumentException("에러!");
    }
}
