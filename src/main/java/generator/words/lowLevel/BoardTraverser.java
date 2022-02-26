package generator.words.lowLevel;

import board.board.Board;
import board.board.fields.Field;
import lombok.experimental.UtilityClass;

import java.util.Optional;

import static util.logic.LogicalExpressions.not;

@UtilityClass
public class BoardTraverser {

    public int goAllTheWayLeft(Board board, int startRow, int startCol) {
        int col = startCol - 1;
        int travelled = 0;
        Optional<Field> leftNeighbour = board.checkField(startRow, col);
        while (leftNeighbour.isPresent()) {
            Field leftField = leftNeighbour.get();
            if (not(leftField.isEmpty())) {
                break;
            }
            travelled++;
            col--;
            leftNeighbour = board.checkField(startRow, col);
        }
        return travelled;
    }

    public int goAllTheWayUp(Board board, int startRow, int startCol) {
        int row = startRow - 1;
        int travelled = 0;
        Optional<Field> upperNeighbour = board.checkField(row, startCol);
        while (upperNeighbour.isPresent()) {
            Field upperField = upperNeighbour.get();
            if (not(upperField.isEmpty())) {
                break;
            }
            travelled++;
            row--;
            upperNeighbour = board.checkField(row, startCol);
        }
        return travelled;
    }
}
