package board.board;


import board.board.fields.Field;
import board.board.fields.PlacementType;
import board.words.Word;
import generator.possibilities.PossibilitiesFactory;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static board.board.fields.Field.assignField;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;

@Value
@Builder
public class Board implements Serializable {

    Field[][] board;
    List<Word> words;

    public Optional<Field> checkField(int row, int col) {
        return indicesCorrect(row, col) ?
                Optional.of(board[row][col]) :
                Optional.empty();
    }

    public Optional<Field> checkField(Coordinate coordinate) {
        return indicesCorrect(coordinate.row(), coordinate.col()) ?
                Optional.of(board[coordinate.col()][coordinate.row()]) :
                Optional.empty();
    }

    public Board addWords(List<Word> words) {
        return Boards.addWord(words, this);
    }

    public Board addWords(Word word) {
        return Boards.addWord(singletonList(word), this);
    }

    void attemptAddition(Word word) {
        switch (word.getType()) {
            case VERTICAL -> addWordVertically(word.getStart(), word.getVectorNo(), word.stringForm());
            case HORIZONTAL -> addWordHorizontally(word.getVectorNo(), word.getStart(), word.stringForm());
        }
        words.add(word);

    }

    private void updatePossibilities(PossibilitiesFactory.StampedAnchor stamped) {
        checkField(stamped.row(), stamped.col())
                .ifPresent(field -> field.setPossibilities(stamped.possibilities()));
    }

    public void updatePossibilities(Set<PossibilitiesFactory.StampedAnchor> stamps) {
        stamps.forEach(this::updatePossibilities);
    }

    public Set<Anchor> getAnchors() {
        return BoardUtil.getAnchors(words).stream()
                .filter(BoardUtil.isAnchorViable(this))
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
        return BoardUtil.generateScrabbleBoard();
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
