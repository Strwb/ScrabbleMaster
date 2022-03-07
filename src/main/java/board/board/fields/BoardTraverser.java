package board.board.fields;

import board.board.Board;
import board.board.Coordinate;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static util.logic.LogicalExpressions.not;

@UtilityClass
public class BoardTraverser {

    public List<Field> goAllTheWayLeft(Coordinate coordinate, Board board) {
        List<Field> fields = new ArrayList<>();
        int row = coordinate.row();
        int col = coordinate.col();

        Optional<Field> field = board.checkField(row, col);

        while (not(field.isEmpty()) && not(field.get().isEmpty())) {
            fields.add(field.get());
            col--;
            field = board.checkField(row, col);
        }
        return fields;
    }

    public List<Field> goAllTheWayUp(Coordinate coordinate, Board board) {
        List<Field> fields = new ArrayList<>();
        int row = coordinate.row();
        int col = coordinate.col();

        Optional<Field> field = board.checkField(row, col);

        while (not(field.isEmpty()) && not(field.get().isEmpty())) {
            fields.add(field.get());
            row--;
            field = board.checkField(row, col);
        }
        return fields;
    }

    public List<Field> goAllTheWayDown(Coordinate coordinate, Board board) {
        List<Field> fields = new ArrayList<>();
        int row = coordinate.row();
        int col = coordinate.col();

        Optional<Field> field = board.checkField(row, col);

        while (not(field.isEmpty()) && not(field.get().isEmpty())) {
            fields.add(field.get());
            row++;
            field = board.checkField(row, col);
        }
        return fields;
    }

    public List<Field> goAllTheWayRight(Coordinate coordinate, Board board) {
        List<Field> fields = new ArrayList<>();
        int row = coordinate.row();
        int col = coordinate.col();

        Optional<Field> field = board.checkField(row, col);

        while (field.isPresent() && field.get().isOccupied()) {
            fields.add(field.get());
            col++;
            field = board.checkField(row, col);
        }
        return fields;
    }
    public int getEmptyLeftDistance(Board board, int startRow, int startCol) {
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

    public int getEmptyAboveDistance(Board board, int startRow, int startCol) {
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

    public int getOccupiedLeftDistance(Board board, int startRow, int startCol) {
        int col = startCol - 1;
        int travelled = 0;
        Optional<Field> leftNeighbour = board.checkField(startRow, col);
        while (leftNeighbour.isPresent()) {
            Field leftField = leftNeighbour.get();
            if (leftField.isEmpty()) {
                break;
            }
            travelled++;
            col--;
            leftNeighbour = board.checkField(startRow, col);
        }
        return travelled;
    }

    public int getOccupiedAboveDistance(Board board, int startRow, int startCol) {
        int row = startRow - 1;
        int travelled = 0;
        Optional<Field> upperNeighbour = board.checkField(row, startCol);
        while (upperNeighbour.isPresent()) {
            Field upperField = upperNeighbour.get();
            if (upperField.isEmpty()) {
                break;
            }
            travelled++;
            row--;
            upperNeighbour = board.checkField(row, startCol);
        }
        return travelled;
    }
}
