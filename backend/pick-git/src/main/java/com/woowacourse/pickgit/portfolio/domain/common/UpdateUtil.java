package com.woowacourse.pickgit.portfolio.domain.common;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class UpdateUtil {

    private UpdateUtil() {
    }

    public static <T extends Updatable<T>> void execute(List<T> origins, List<T> sources) {
        List<UpdatableProxy<T>> originProxies = toProxy(origins);
        List<UpdatableProxy<T>> sourceProxies = toProxy(sources);

        delete(originProxies, sourceProxies);
        update(originProxies, sourceProxies);
        create(originProxies, sourceProxies);

        reset(origins, originProxies);
    }

    private static <T extends Updatable<T>> List<UpdatableProxy<T>> toProxy(List<T> values) {
        return values.stream()
            .map(UpdatableProxy::new)
            .collect(toList());
    }

    private static <T extends Updatable<T>> void delete(
        List<UpdatableProxy<T>> originProxies,
        List<UpdatableProxy<T>> sourceProxies
    ) {
        originProxies.removeIf(origin -> !sourceProxies.contains(origin));
    }

    private static <T extends Updatable<T>> void update(
        List<UpdatableProxy<T>> originProxies,
        List<UpdatableProxy<T>> sourceProxies
    ) {
        Map<UpdatableProxy<T>, UpdatableProxy<T>> originsWithProxy = originProxies.stream()
            .collect(toMap(Function.identity(), Function.identity()));

        List<UpdatableProxy<T>> updatableSources = sourceProxies.stream()
            .filter(originsWithProxy::containsKey)
            .collect(toList());
        updatableSources.forEach(sourceProxy -> originsWithProxy.get(sourceProxy).update(sourceProxy));

        sourceProxies.removeAll(updatableSources);
    }

    private static <T extends Updatable<T>> void create(
        List<UpdatableProxy<T>> originProxies,
        List<UpdatableProxy<T>> sourceProxies
    ) {
       originProxies.addAll(sourceProxies);
    }

    private static <T extends Updatable<T>> void reset(
        List<T> origins,
        List<UpdatableProxy<T>> originProxies
    ) {
        origins.clear();

        List<T> targets = getTargets(originProxies);

        origins.addAll(targets);
    }

    private static <T extends Updatable<T>> List<T> getTargets(
        List<UpdatableProxy<T>> originProxies
    ) {
        return originProxies.stream()
            .map(UpdatableProxy::getTarget)
            .collect(toList());
    }
}
