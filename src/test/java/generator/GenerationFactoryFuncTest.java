package generator;

import board.board.Board;
import board.words.Word;
import dictionary.ScrabbleDictionary;
import generator.words.GenerationFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import player.letters.Rack;

import java.util.List;
import java.util.Optional;

import static board.board.Board.freshScrabbleBoard;
import static dictionary.ScrabbleDictionary.scrabbleDictionary;
import static org.assertj.core.api.Assertions.assertThat;
import static shared.TestEntities.*;

public class GenerationFactoryFuncTest {

    private static GenerationFactory generationFactory;

    @BeforeAll
    public static void setup() {
        ScrabbleDictionary dictionary = scrabbleDictionary();
        dictionary.loadDictionary();
        generationFactory = new GenerationFactory(dictionary);
    }

    @Test
    public void shouldAddWordAndFindAnchors() {
        Board board = freshScrabbleBoard();
        List<Word> words = createTwoTestWordsHorizontalScenario(board);
        board = board.addWords(words);
        var anchors = board.getAnchors();
        assertThat(anchors.size()).isNotZero();
    }

    @Test
    public void shouldCalculateWordScore() {
        Board board = freshScrabbleBoard();
        Word pieniadz = highScoringWord(board);
        assertThat(pieniadz.getWordScore()).isEqualTo(45);
    }

    @Test
    public void shouldCalculateMove() {

        Board board = freshScrabbleBoard();
        List<Word> words = createTwoTestWordsHorizontalScenario(board);

        board = board.addWords(words);

        var anchors = board.getAnchors();
        Rack rack = createTestRack();

        board.printBoard();
        Optional<Word> word = generationFactory.findNextMove(board, rack, anchors);

        assertResultIsKefia(word);

        board = board.addWords(word.get());
        board.printBoard();
    }

    @Test
    public void shouldCalculateVerticalMove() {

        Board board = freshScrabbleBoard();
        List<Word> words = createTwoTestWordsVerticalScenario(board);

        board = board.addWords(words);

        var anchors = board.getAnchors();
        Rack rack = createTestRack();

        board.printBoard();
        Optional<Word> word = generationFactory.findNextMove(board, rack, anchors);

        assertResultIsKefia(word);

        board = board.addWords(word.get());
        board.printBoard();
    }

    private void assertResultIsKefia(Optional<Word> word) {
        assertThat(word.isPresent() && "kefią".equals(word.get().stringForm())).isTrue();
    }


}
