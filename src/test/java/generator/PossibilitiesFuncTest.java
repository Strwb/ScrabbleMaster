package generator;

import board.board.Board;
import board.words.Word;
import dictionary.ScrabbleDictionary;
import generator.possibilities.PossibilitiesFactory;
import generator.words.GenerationFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import player.letters.Rack;
import shared.TestEntities;

import java.util.List;
import java.util.Optional;

import static board.board.Board.freshScrabbleBoard;
import static dictionary.ScrabbleDictionary.scrabbleDictionary;
import static org.assertj.core.api.Assertions.assertThat;
import static shared.TestEntities.createTestRack;
import static shared.TestEntities.createTwoTestWords;

public class PossibilitiesFuncTest {

    private static GenerationFactory generationFactory;
    private static PossibilitiesFactory possibilitiesFactory;

    @BeforeAll
    public static void setup() {
        ScrabbleDictionary dictionary = scrabbleDictionary();
        dictionary.loadDictionary();
        generationFactory = new GenerationFactory(dictionary);
        possibilitiesFactory = new PossibilitiesFactory(dictionary);
    }

    @Test
    public void shouldFindPossibilitiesAroundNewWord() {
        Board board = freshScrabbleBoard();
        Word word = TestEntities.highScoringWord(board);
        board = board.addWords(word);
        board.printBoard();
        var anchors = board.getAnchors();
        var stamped = possibilitiesFactory.generatePossibilities(anchors, board);
        System.out.println(stamped);
        assertThat(stamped.size()).isGreaterThan(0);
    }

    @Test
    public void shouldUseCalculatedPossibilities() {
        Board board = freshScrabbleBoard();
        List<Word> words = createTwoTestWords(board);
        board = board.addWords(words);
        var anchors = board.getAnchors();
        Rack rack = createTestRack();

        possibilitiesFactory.generatePossibilities(anchors, board);

        //TODO:
        // - calculate possibilities after 2 test words were added
        // - generate word

        board.printBoard();
        Optional<Word> word = generationFactory.findNextMove(board, rack, anchors);



//        board.addWord(word.get());
        board = board.addWords(word.get());
        board.printBoard();
    }
}
