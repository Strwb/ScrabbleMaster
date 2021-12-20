package util.lists;

import java.util.ArrayList;
import java.util.List;

public class Lists {

    public static <T> List<T> modifiableEmptyList() {
        return new ArrayList<>();
    }

    public static <T> List<T> modifiableCopyOf(List<T> list) {
        return new ArrayList<>(List.copyOf(list));
    }
}
