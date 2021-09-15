package com.woowacourse.pickgit.authentication.infrastructure.token;

public enum TokenBodyType {
    USERNAME("username"),
    TOKEN_ID("tokenId");

    private final String value;

    TokenBodyType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
