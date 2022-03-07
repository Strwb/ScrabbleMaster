package simulation;

import board.board.Board;
import board.words.Word;
import dictionary.ScrabbleDictionary;
import generator.possibilities.PossibilitiesFactory;
import generator.words.GenerationFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import player.letters.LetterBag;
import player.letters.Rack;
import player.letters.RackUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static board.board.Board.freshScrabbleBoard;
import static dictionary.ScrabbleDictionary.scrabbleDictionary;
import static org.assertj.core.api.Assertions.assertThat;
import static shared.TestEntities.createTwoTestWordsHorizontalScenario;

public class LongTermSimulation {

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
    public void shouldPerformLongTermSimulation() {

        Board board = freshScrabbleBoard();
        List<Word> words = createTwoTestWordsHorizontalScenario(board);
        LetterBag bag = LetterBag.initialBag();
        board = board.addWords(words);
        Rack playerRack = RackUtil.randomRack(bag);
        Rack opponentRack = RackUtil.randomRack(bag);

        board.updatePossibilities(possibilitiesFactory.generatePossibilities(board.getAnchors(), board));

        System.out.println("START BOARD");
        board.printBoard();

        List<Word> generatedWords = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Optional<Word> playerWord = simulation.simulate(board.clone(), playerRack);
            if (playerWord.isPresent()) {
                board = board.addWords(playerWord.get());
                board.updatePossibilities(possibilitiesFactory.generatePossibilities(board.getAnchors(), board));
                System.out.println("PLAYER MOVE");
                board.printBoard();
                playerRack = playerRack.withoutLetters(playerWord.get());
                playerRack = RackUtil.drawLetters(bag, playerRack);
                generatedWords.add(playerWord.get());
            }


            Optional<Word> opponentWord = simulation.simulate(board.clone(), opponentRack);
            if (opponentWord.isPresent()) {
                board = board.addWords(opponentWord.get());
                board.updatePossibilities(possibilitiesFactory.generatePossibilities(board.getAnchors(), board));
                System.out.println("OPPONENT MOVE");
                board.printBoard();
                opponentRack = opponentRack.withoutLetters(opponentWord.get());
                opponentRack = RackUtil.drawLetters(bag, opponentRack);
                generatedWords.add(opponentWord.get());
            }

        }



        board.printBoard();

        assertThat(generatedWords).isNotEmpty();
    }
}
