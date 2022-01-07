package board;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static board.Board.Anchor;
import static board.Field.*;
import static board.PlacementType.HORIZONTAL;
import static board.PlacementType.VERTICAL;
import static util.lists.Lists.modifiableEmptyList;
import static util.logic.LogicalExpressions.not;
import static util.sets.Sets.modifiableEmptySet;

@UtilityClass
public class BoardUtil {

    static Board generateScrabbleBoard() {
        Field[][] board = new Field[15][15];
        for (Field[] row : board) {
            Arrays.fill(row, emptyField());
        }
        return Board.builder()
                .board(setBonuses(board))
                .words(modifiableEmptyList())
                .build();
    }

    static Set<Anchor> getAnchors(List<Word> words) {
        Set<Anchor> anchors = modifiableEmptySet();
        words.forEach(word -> {
            anchors.addAll(getAnchors(word));
        });
        return anchors;
    }

    static Set<Anchor> getAnchors(Word word) {
        return switch (word.getType()) {
            case VERTICAL ->  verticalAnchors(word);
            case HORIZONTAL -> horizontalAnchors(word);
        };
    }

    private static Field[][] setBonuses(Field[][] board) {
        board[0][0] = tripleWordField();
        board[0][3] = doubleLetterField();
        board[0][7] = tripleWordField();
        board[0][11] = doubleLetterField();
        board[0][14] = tripleWordField();

        board[1][1] = doubleWordField();
        board[1][5] = tripleLetterField();
        board[1][9] = tripleLetterField();
        board[1][13] = doubleWordField();

        board[2][2] = doubleWordField();
        board[2][6] = doubleLetterField();
        board[2][8] = doubleLetterField();
        board[2][12] = doubleWordField();

        board[3][0] = doubleLetterField();
        board[3][3] = doubleWordField();
        board[3][7] = doubleLetterField();
        board[3][11] = doubleWordField();
        board[3][14] = doubleLetterField();

        board[4][4] = doubleWordField();
        board[4][10] = doubleWordField();

        board[5][1] = tripleLetterField();
        board[5][5] = tripleLetterField();
        board[5][9] = tripleLetterField();
        board[5][13] = tripleLetterField();

        board[6][2] = doubleLetterField();
        board[6][6] = doubleLetterField();
        board[6][8] = doubleLetterField();
        board[6][12] = doubleLetterField();

        board[7][0] = tripleWordField();
        board[7][3] = doubleLetterField();
        board[7][11] = doubleLetterField();
        board[7][14] = tripleWordField();

        board[8][2] = doubleLetterField();
        board[8][6] = doubleLetterField();
        board[8][8] = doubleLetterField();
        board[8][12] = doubleLetterField();

        board[9][1] = tripleLetterField();
        board[9][5] = tripleLetterField();
        board[9][9] = tripleLetterField();

        board[10][4] = doubleWordField();
        board[10][10] = doubleWordField();

        board[11][0] = doubleLetterField();
        board[11][3] = doubleWordField();
        board[11][7] = doubleLetterField();
        board[11][11] = doubleWordField();
        board[11][14] = doubleLetterField();

        board[12][2] = doubleWordField();
        board[12][6] = doubleLetterField();
        board[12][8] = doubleLetterField();
        board[12][12] = doubleWordField();

        board[13][1] = doubleWordField();
        board[13][5] = tripleLetterField();
        board[13][9] = tripleLetterField();
        board[13][13] = doubleWordField();

        board[14][0] = tripleWordField();
        board[14][3] = doubleLetterField();
        board[14][7] = tripleWordField();
        board[14][11] = doubleLetterField();
        board[14][14] = tripleWordField();

        return board;
    }

    private static Set<Anchor> verticalAnchors(Word word) {
        Set<Anchor> anchors = verticalSides(word);
        anchors.addAll(
                Set.of(
                        new Anchor(word.getStart() - 1, word.getVectorNo(), VERTICAL),
                        new Anchor(word.getStart() + word.getLength(), word.getVectorNo(), VERTICAL)
                )
        );
        return anchors;
    }

    private static Set<Anchor> verticalSides(Word word) {
        int col = word.getVectorNo();
        Set<Anchor> anchors = modifiableEmptySet();
        for (int row = word.getStart(); row < word.getStart() + word.getLength(); row++) {
            anchors.add(new Anchor(row, col - 1, HORIZONTAL));
            anchors.add(new Anchor(row, col + 1, HORIZONTAL));
        }
        return anchors;
    }

    private static Set<Anchor> horizontalAnchors(Word word) {
        Set<Anchor> anchors = horizontalSides(word);
        anchors.addAll(
                Set.of(
                        new Anchor(word.getVectorNo(), word.getStart() - 1, HORIZONTAL),
                        new Anchor(word.getVectorNo(), word.getStart() + word.getLength(), HORIZONTAL)
                )
        );
        return anchors;
    }

    private static Set<Anchor> horizontalSides(Word word) {
        int row = word.getVectorNo();
        Set<Anchor> anchors = modifiableEmptySet();
        for (int col = word.getStart(); col < word.getStart() + word.getLength(); col++) {
            anchors.add(new Anchor(row - 1, col, VERTICAL));
            anchors.add(new Anchor(row + 1, col, VERTICAL));
        }
        return anchors;
    }

    public static Predicate<Anchor> isAnchorViable(Board board) {
        return anchor -> not(
                anchor.row() < 0 ||
                        anchor.row() >= 15 ||
                        anchor.col() < 0 ||
                        anchor.col() >= 15 ||
                        not(board.getBoard()[anchor.row()][anchor.col()].isEmpty())
        );
    }
}
