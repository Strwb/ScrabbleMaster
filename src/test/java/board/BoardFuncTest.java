package board;

import board.board.Board;
import board.words.Word;
import org.junit.jupiter.api.Test;

import java.util.List;

import static board.board.Board.freshScrabbleBoard;
import static board.board.fields.Field.*;
import static board.board.fields.FieldBonus.NONE;
import static org.assertj.core.api.Assertions.assertThat;
import static shared.TestEntities.createTwoTestWords;

public class BoardFuncTest {

    @Test
    public void shouldCreateEmptyBoard() {
        Board board = freshScrabbleBoard();
        assertThat(board.checkField(0,0).get()).isEqualTo(tripleWordField());
        assertThat(board.checkField(9, 5).get()).isEqualTo(tripleLetterField());
        assertThat(board.checkField(10, 10).get()).isEqualTo(doubleWordField());
    }

    @Test
    public void shouldAddWords() {
        Board board = freshScrabbleBoard();
        List<Word> words = createTwoTestWords(board);
//        words.forEach(board::addWord);
        board = board.addWords(words);
        assertThat(board.checkField(7, 7).get().getValue()).isEqualTo(Character.valueOf('d'));
        assertThat(board.checkField(7, 8).get().getValue()).isEqualTo(Character.valueOf('u'));
        assertThat(board.checkField(7, 9).get().getValue()).isEqualTo(Character.valueOf('p'));
        assertThat(board.checkField(7, 10).get().getValue()).isEqualTo(Character.valueOf('a'));

        assertThat(board.checkField(8, 8).get().getValue()).isEqualTo(Character.valueOf('r'));
        assertThat(board.checkField(9, 8).get().getValue()).isEqualTo(Character.valueOf('n'));
        assertThat(board.checkField(10, 8).get().getValue()).isEqualTo(Character.valueOf('a'));

        assertThat(board.checkField(8, 8).get().getBonus()).isEqualTo(NONE);
    }

    @Test
    public void shouldUpdateCrossWords() {
        Board board = freshScrabbleBoard();
    }
}
