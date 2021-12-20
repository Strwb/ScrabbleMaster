package board;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static board.BoardUtil.generateScrabbleBoard;
import static board.BoardUtil.isAnchorViable;
import static board.Field.assignField;
import static java.util.stream.Collectors.toSet;

@Value
@Builder
public class Board {

    Field[][] board;
    List<Word> words;

    public Optional<Field> checkField(int row, int col) {
        return indicesCorrect(row, col) ?
                Optional.of(board[row][col]) :
                Optional.empty();
    }

    public void addWord(Word word) {
        switch (word.getType()) {
            case VERTICAL -> addWordVertically(word.getStart(), word.getVectorNo(), word.stringForm());
            case HORIZONTAL -> addWordHorizontally(word.getVectorNo(), word.getStart(), word.stringForm());
        }
        words.add(word);
    }

    public static Board freshScrabbleBoard() {
        return generateScrabbleBoard();
    }

    public Set<Anchor> getAnchors() {
        return BoardUtil.getAnchors(words).stream()
                .filter(isAnchorViable(this))
                .collect(toSet());
    }

    public void printBoard() {
        StringBuilder sb = new StringBuilder();
        sb.append("BOARD: \n");
        for (Field[] row : board) {
            for (Field field : row) {
                sb.append(field.getValue() + " ");
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }

    private void setField(int row, int col, Character value) {
        if (indicesCorrect(row, col) && board[row][col].getValue().equals('/')) {
            board[row][col] = assignField(value);
        }
    }

    private void addWordVertically(int startRow, int startCol, String word) {
        int charIndex = 0;
        for (int row = startRow; row < startRow + word.length(); row++) {
            setField(row, startCol, word.charAt(charIndex));
            charIndex++;
        }
    }

    private void addWordHorizontally(int startRow, int startCol, String word) {
        int charIndex = 0;
        for (int col = startCol; col < startCol + word.length(); col++) {
            setField(startRow, col, word.charAt(charIndex));
            charIndex++;
        }
    }

//    private Set<Character> calculatePossibilities(int startRow, int startCol, String word, PlacementType type) {
//        switch (type) {
//            case HORIZONTAL -> {
//                return possibilitiesHorizontal(startRow, startCol, word);
//            }
//            case VERTICAL -> {
//                return possibilitiesVertical(startRow, startCol, word);
//            }
//        }
//    }
//
//    private Set<Character> possibilitiesHorizontal(int startRow, int startCol, String word) {
//        Field field = board[startRow][startCol];
//        var pre = new Coordinate(startRow, startCol - 1);
//        var post = new Coordinate(startRow, startCol + word.length());
//    }
//
//    private Set<Character> possibilitiesVertical(int startRow, int startCol, String word) {
//        Field field = board[startRow][startCol];
//    }

    private static boolean indicesCorrect(int row, int col) {
        return (row >= 0) && (row < 15) && (col >= 0) && (col < 15);
    }

    public record Anchor(int row, int col, PlacementType type) {
    }

    private record Coordinate(int row, int col) {

        boolean isCorrect() {
            return row < 0 ||
                    row >= 15 ||
                    col < 0 ||
                    col >= 15;

        }
    }
}
