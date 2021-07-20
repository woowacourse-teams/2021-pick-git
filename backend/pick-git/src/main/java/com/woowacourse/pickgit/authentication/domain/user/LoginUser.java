package com.woowacourse.pickgit.authentication.domain.user;

public class LoginUser extends AppUser {

    public LoginUser(String username, String accessToken) {
        super(username, accessToken);
    }

    @Override
    public boolean isGuest() {
        return false;
    }
}
