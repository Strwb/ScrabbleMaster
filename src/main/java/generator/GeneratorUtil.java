package generator;

import board.Board;
import board.Board.Anchor;
import board.Field;
import board.Word;
import dictionary.ScrabbleDictionary;
import player.Rack;
import util.lists.Lists;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Predicate;

import static board.PlacementType.HORIZONTAL;
import static board.PlacementType.VERTICAL;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static util.lists.Lists.modifiableEmptyList;
import static util.logic.LogicalExpressions.not;

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

    static Predicate<Word> overrideAttempt(Board board) {
        return word -> {
            int row = word.getVectorNo();
            int col = word.getStart();
            for (int i = 0; i < word.getLetters().size(); i++) {
                Field letter = word.getLetters().get(i);
                Optional<Field> boardLetter = board.checkField(row, col);

                if (boardLetter.isEmpty()) {
                    return false;
                }

                Field boardField = boardLetter.get();

                if (not(boardField.isEmpty())) {

                    boolean overrideAttempt = boardField.getValue() != letter.getValue();

                    if (overrideAttempt) {
                        return false;
                    }
                }

                col++;
            }
            return true;
        };
    }

    static HorizontalWordGenerator horizontalGenerator(Anchor anchor, Board board, Rack rack, ScrabbleDictionary dictionary) {
        return HorizontalWordGenerator.builder()
                        .dictionary(dictionary)
                        .anchorRow(anchor.row())
                        .anchorCol(anchor.col())
                        .board(board)
                        .startingRack(rack)
                        .generatedWords(modifiableEmptyList())
                        .build();
    }

    static VerticalWordGenerator verticalGenerator(Anchor anchor, Board board, Rack rack, ScrabbleDictionary dictionary) {
        return VerticalWordGenerator.builder()
                .dictionary(dictionary)
                .anchorRow(anchor.row())
                .anchorCol(anchor.col())
                .board(board)
                .startingRack(rack)
                .generatedWords(modifiableEmptyList())
                .build();
    }
}
