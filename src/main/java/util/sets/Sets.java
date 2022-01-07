package util.sets;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class Sets {

    public static <T> Set<T> modifiableEmptySet() {
        return new HashSet<>();
    }

    public static <T> Set<T> merge(Set<T> set1, Set<T> set2) {
        return Stream.of(set1, set2)
                .flatMap(Collection::stream)
                .collect(toSet());
    }

    public static <T> Set<T> modifiableCopySet(Set<T> set) {
        return new HashSet<>(Set.copyOf(set));
    }

    public static <T> Set<T> setOrIfEmpty(Set<T> target, Set<T> alternative) {
        return target.isEmpty() ?
                alternative :
                target;
    }
}
