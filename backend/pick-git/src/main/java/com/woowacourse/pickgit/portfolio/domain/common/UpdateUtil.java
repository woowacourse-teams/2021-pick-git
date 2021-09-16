package com.woowacourse.pickgit.portfolio.domain.common;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class UpdateUtil {

    private UpdateUtil() {
    }

    public static <T extends Updatable<T>> void execute(List<T> origins, List<T> sources) {
        update(origins, sources);
        delete(origins, sources);
        create(origins, sources);
    }

    private static <T extends Updatable<T>> void update(List<T> origins, List<T> sources) {
        Map<T, T> allOrigins = origins.stream()
            .collect(toMap(Function.identity(), Function.identity()));

        sources.stream()
            .filter(allOrigins::containsKey)
            .forEach(source -> allOrigins.get(source).update(source));
    }

    private static <T extends Updatable<T>> void delete(List<T> origins, List<T> sources) {
        origins.removeIf(origin -> !sources.contains(origin));
    }

    private static <T extends Updatable<T>> void create(List<T> origins, List<T> sources) {
        sources.stream()
            .filter(source -> !origins.contains(source))
            .forEach(origins::add);
    }
}
