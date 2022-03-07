package generator.possibilities;

import board.board.Board;
import board.board.Coordinate;
import board.board.fields.BoardTraverser;
import board.board.fields.Field;
import board.words.WordUtil;
import dictionary.ScrabbleDictionary;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static board.words.WordUtil.wholeAlphabet;
import static java.util.Collections.reverse;
import static util.logic.LogicalExpressions.not;

@Builder
@Value
public class LeftPossibilities implements PossibilitiesFinder{

    ScrabbleDictionary dictionary;
    Coordinate coordinate;
    Board board;

    @Override
    public Set<Character> findPossibilities() {
        return left();
    }

    private Set<Character> left() {
        int row = coordinate.row();
        int col = coordinate.col() - 1;

        Optional<Field> left = board.checkField(row, col);

        if (left.isPresent() && not(left.get().isEmpty())) {
            return calculatePossibilities();
        }

        return wholeAlphabet();
    }

    private Set<Character> calculatePossibilities() {
        List<Field> fields = BoardTraverser.goAllTheWayLeft(coordinate, board);
        reverse(fields);
        return dictionary.findWord(WordUtil.stringForm(fields))
                .getNeighbours()
                .keySet();
    }
//
//    private List<Field> goAllTheWayLeft() {
//        List<Field> fields = new ArrayList<>();
//        int row = coordinate.row();
//        int col = coordinate.col() - 1;
//
//        Optional<Field> field = board.checkField(row, col);
//
//        while (not(field.isEmpty()) && not(field.get().isEmpty())) {
//            fields.add(field.get());
//            col--;
//            field = board.checkField(row, col);
//        }
//        return fields;
//    }
}
