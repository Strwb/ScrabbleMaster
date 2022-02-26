package generator.words;

import board.board.Board;
import board.board.Board.Anchor;
import board.board.fields.Field;
import board.words.Word;
import board.words.Word.WordCandidate;
import generator.words.lowLevel.SideGenerator;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static board.board.fields.PlacementType.HORIZONTAL;
import static board.board.fields.PlacementType.VERTICAL;
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

    public static Optional<Word> getMaxWord(List<Word> results) {
        return results.stream()
                .max(Comparator.comparing(Word::getWordScore));
    }

    public static List<WordCandidate> getGeneratedWords(List<SideGenerator.Generator> results) {
        return extractWords(results);
    }

    private static List<WordCandidate> extractWords(List<SideGenerator.Generator> results) {
        return results.stream()
                .map(SideGenerator.Generator::generateWords)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    static List<Word> highScoringWord(List<WordCandidate> candidates) {
        List<Word> result = modifiableEmptyList();
        int i = 0;
        while ((i < candidates.size()) && (i < 20)) {
            result.add(candidates.get(i).extractWord());
           i++;
        }
        return result;
    }

    public static Predicate<Word> notOverride(Board board) {
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

                    // do we try to put different letter at field that is already taken?
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
}
