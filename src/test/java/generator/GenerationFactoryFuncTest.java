package generator;

import board.Board;
import board.Word;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import player.Rack;

import java.util.List;
import java.util.Optional;

import static board.Board.freshScrabbleBoard;
import static org.assertj.core.api.Assertions.assertThat;
import static shared.TestEntities.*;

public class GenerationFactoryFuncTest {

    private static GenerationFactory generationFactory;

    @BeforeAll
    public static void setup() {
        generationFactory = new GenerationFactory();
    }

    @Test
    public void shouldAddWordAndFindAnchors() {
        Board board = freshScrabbleBoard();
        List<Word> words = createTwoTestWords(board);
        words.forEach(board::addWord);
        var anchors = board.getAnchors();
        assertThat(anchors.size()).isNotZero();
    }

    @Test
    public void shouldCalculateWordScore() {
        Board board = freshScrabbleBoard();
        Word pieniadz = highScoringWord(board);
        assertThat(pieniadz.getScore()).isEqualTo(45);
    }

    @Test
    public void shouldCalculateMove() {

        Board board = freshScrabbleBoard();
        List<Word> words = createTwoTestWords(board);
        words.forEach(board::addWord);
        var anchors = board.getAnchors();
        Rack rack = createTestRack();

        board.printBoard();
        Optional<Word> word = generationFactory.findNextMove(board, rack, anchors);

        assertResultIsKefia(word);

        board.addWord(word.get());
        board.printBoard();
    }

    private void assertResultIsKefia(Optional<Word> word) {
        assertThat(word.isPresent() && "kefią".equals(word.get().stringForm())).isTrue();
    }


}
