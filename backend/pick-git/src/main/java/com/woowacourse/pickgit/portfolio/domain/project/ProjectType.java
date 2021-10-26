package com.woowacourse.pickgit.portfolio.domain.project;

import com.woowacourse.pickgit.exception.portfolio.ProjectTypeNotFoundException;
import java.util.Arrays;

public enum ProjectType {
    TEAM("team"),
    PERSONAL("personal");

    private final String value;

    ProjectType(String value) {
        this.value = value;
    }

    public static ProjectType of(String source) {
        return Arrays.stream(ProjectType.values())
            .filter(value -> value.getValue().equals(source))
            .findAny()
            .orElseThrow(ProjectTypeNotFoundException::new);
    }

    public String
    getValue() {
        return value;
    }
}
