package generator;

import board.Board.Anchor;
import board.Word;
import util.lists.Lists;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static board.PlacementType.HORIZONTAL;
import static board.PlacementType.VERTICAL;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class GeneratorUtil {

    public static Set<Anchor> horizontalAnchors(Set<Anchor> anchors) {
        return anchors.stream()
                .filter(anchor -> anchor.type() == HORIZONTAL)
                .collect(toSet());
    }

    public static Set<Anchor> verticalAnchors(Set<Anchor> anchors) {
        return anchors.stream()
                .filter(anchor -> anchor.type() == VERTICAL)
                .collect(toSet());
    }

    public static Optional<Word> getMaxWord(List<Future<List<Word>>> futures) {
        return extractWordsFromFutures(futures).stream()
                .max(Comparator.comparing(Word::getScore));
    }

    private static List<Word> extractWordsFromFutures(List<Future<List<Word>>> futures) {
        List<List<Word>> words = Lists.modifiableEmptyList();
        futures.forEach(future -> {
            try {
                words.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        return words.stream()
                .flatMap(Collection::stream)
                .collect(toList());
    }
}
