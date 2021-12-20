package generator;

import board.Board;
import board.Word;
import org.junit.jupiter.api.Test;
import player.Rack;

import java.util.List;
import java.util.Optional;

import static board.Board.freshScrabbleBoard;
import static org.assertj.core.api.Assertions.assertThat;
import static shared.TestEntities.*;

public class GenerationFactoryFuncTest {

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

        GenerationFactory factory = new GenerationFactory();
        Optional<Word> word = factory.findNextMove(board, rack, anchors);
        assertThat(word.isPresent()).isTrue();
        board.addWord(word.get());
        board.printBoard();
        System.out.println("SCORE: " + word.get().getScore());
    }


}
