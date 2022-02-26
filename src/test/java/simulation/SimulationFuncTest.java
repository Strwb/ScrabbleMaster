package simulation;

import board.board.Board;
import board.words.Word;
import dictionary.ScrabbleDictionary;
import generator.possibilities.PossibilitiesFactory;
import generator.words.GenerationFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import player.letters.Rack;

import java.util.List;
import java.util.Optional;

import static board.board.Board.freshScrabbleBoard;
import static dictionary.ScrabbleDictionary.scrabbleDictionary;
import static org.assertj.core.api.Assertions.assertThat;
import static shared.TestEntities.createTestRack;
import static shared.TestEntities.createTwoTestWordsHorizontalScenario;

public class SimulationFuncTest {

    private static GenerationFactory generationFactory;
    private static PossibilitiesFactory possibilitiesFactory;
    private static Simulation simulation;

    @BeforeAll
    public static void setup() {
        ScrabbleDictionary dictionary = scrabbleDictionary();
        dictionary.loadDictionary();
        generationFactory = new GenerationFactory(dictionary);
        possibilitiesFactory = new PossibilitiesFactory(dictionary);
        simulation = new Simulation(generationFactory, possibilitiesFactory);
    }

    @Test
    public void shouldFindBestWord() {

        Board board = freshScrabbleBoard();
        List<Word> words = createTwoTestWordsHorizontalScenario(board);
        board = board.addWords(words);
        var anchors = board.getAnchors();
        Rack rack = createTestRack();

        possibilitiesFactory.generatePossibilities(anchors, board);

        board.printBoard();

        Optional<Word> bestWord = simulation.simulate(board, rack);

        if (bestWord.isPresent()) {
            board = board.addWords(bestWord.get());
        }

        board.printBoard();

        assertThat(bestWord).isPresent();
    }
}
