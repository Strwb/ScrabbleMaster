package generator.possibilities;

import board.board.Board;
import board.board.Coordinate;
import board.board.fields.BoardTraverser;
import board.board.fields.Field;
import dictionary.ScrabbleDictionary;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static board.words.WordUtil.stringForm;
import static board.words.WordUtil.wholeAlphabet;
import static java.util.stream.Collectors.toSet;
import static util.logic.LogicalExpressions.not;

@Builder
@Value
public class DownPossibilities implements PossibilitiesFinder{

    ScrabbleDictionary dictionary;
    Coordinate coordinate;
    Board board;

    @Override
    public Set<Character> findPossibilities() {
        return down();
    }

    private Set<Character> down() {
        int row = coordinate.row() + 1;
        int col = coordinate.col();

        Optional<Field> left = board.checkField(row, col);

        if (left.isPresent() && not(left.get().isEmpty())) {
            return calculatePossibilities();
        }

        return wholeAlphabet();
    }

    private Set<Character> calculatePossibilities() {
        List<Field> fields = BoardTraverser.goAllTheWayDown(coordinate, board);
        Set<Character> wordStarts = wholeAlphabet();
        return wordStarts.stream()
                .filter(start -> isValidBeginning(fields).test(start))
                .collect(toSet());

    }

    private Predicate<Character> isValidBeginning(List<Field> fields) {
        return beginning -> {
            String word = beginning + stringForm(fields);
            return dictionary.findWord(word).isEndOfWord();
        };
    }
//
//    private List<Field> goAllTheWayDown() {
//        List<Field> fields = new ArrayList<>();
//        int row = coordinate.row() + 1;
//        int col = coordinate.col();
//
//        Optional<Field> field = board.checkField(row, col);
//
//        while (not(field.isEmpty()) && not(field.get().isEmpty())) {
//            fields.add(field.get());
//            row++;
//            field = board.checkField(row, col);
//        }
//        return fields;
//    }
}
