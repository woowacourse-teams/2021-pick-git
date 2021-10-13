package com.woowacourse.pickgit.query.fixture;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

public class AllUserAct extends Act{

    private final List<TUser> users;
    private final List<TUser> except;

    public AllUserAct(List<TUser> users) {
        this.users = users;
        this.except = new ArrayList<>();
    }

    public List<TUser> 가져온다() {
        return users.stream()
            .filter(user -> !except.contains(user))
            .collect(toList());
    }

    public List<String> 로그인을한다() {
        return users.stream()
            .filter(user -> !except.contains(user))
            .map(TUser::은로그인을한다)
            .collect(toList());
    }

    public AllUserAct 중이유저는제외하고(TUser... tUsers) {
        except.addAll(List.of(tUsers));
        return this;
    }
}
