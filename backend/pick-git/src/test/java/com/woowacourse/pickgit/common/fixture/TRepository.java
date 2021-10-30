package com.woowacourse.pickgit.common.fixture;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public enum TRepository {
    PICK_GIT(
        new Pair("java", "123"),
        new Pair("spring", "12313"),
        new Pair("god", "5335")
    ),
    PROLOG(
        new Pair("typescript", "1234"),
        new Pair("spring", "645"),
        new Pair("king", "593499)")
    ),
    UNKNOWN;

    private final List<Pair> tags;

    TRepository(Pair... tags) {
        this.tags = List.of(tags);
    }

    public Map<String, String> getTagsAsJson() {
        return tags.stream()
            .collect(toMap(Pair::getName, Pair::getNumber));
    }

    public List<String> getTags() {
        return tags.stream()
            .map(Pair::getName)
            .collect(toList());
    }

    public static boolean exists(String repoName) {
        return Arrays.stream(values())
            .filter(repository -> repository != UNKNOWN)
            .map(TRepository::name)
            .anyMatch(repository -> repository.equals(repoName));
    }

    private static class Pair {
        private final String name;
        private final String number;

        public Pair(String name, String number) {
            this.name = name;
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public String getNumber() {
            return number;
        }
    }
}
