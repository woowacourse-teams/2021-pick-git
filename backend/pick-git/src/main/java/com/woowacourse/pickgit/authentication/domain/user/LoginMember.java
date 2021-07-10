package com.woowacourse.pickgit.authentication.domain.user;

public class LoginMember extends RequestUser {

    public LoginMember(String username, String accessToken) {
        super(username, accessToken);
    }

    @Override
    public boolean isAnonymous() {
        return false;
    }
}
