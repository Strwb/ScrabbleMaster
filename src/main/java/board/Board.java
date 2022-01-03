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
        //TODO Generate possibilities for that word:
        // - Get anchors for that word
        // - Update possibilities for each field, using word generation
    }

    public void updatePossibilities(Set<Character> possibilities, int row, int col) {
        checkField(row, col).ifPresent(field -> field.setPossibilities(possibilities));
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

    public static Board freshScrabbleBoard() {
        return generateScrabbleBoard();
    }

    private void setField(int row, int col, Character value) {
        if (indicesCorrect(row, col) && board[row][col].getValue().equals('/')) {
            board[row][col] = assignField(Character.toLowerCase(value));
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

    private static boolean indicesCorrect(int row, int col) {
        return (row >= 0) && (row < 15) && (col >= 0) && (col < 15);
    }

    public record Anchor(int row, int col, PlacementType type) {
    }

}
