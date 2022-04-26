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

import java.util.Optional;

import static board.board.Board.freshScrabbleBoard;
import static dictionary.ScrabbleDictionary.scrabbleDictionary;

public class ResultsFuncTest {

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
    public void singleStartTest() {

        Board board = freshScrabbleBoard();
        System.out.println("START BOARD");
        board.printBoard();
        board.updatePossibilities(possibilitiesFactory.generatePossibilities(board.getAnchors(), board));
        for (int i = 0; i < 20; i++) {
            singleStartSimulation(board);
        }
    }

    private void singleStartSimulation(Board board) {
        LetterBag bag = LetterBag.initialBag();
        Rack playerRack = RackUtil.randomRack(bag);

        Optional<Word> playerWord = simulation.simulateWithoutScore(board.clone(), playerRack, bag);
        if (playerWord.isPresent()) {
            printMoveHeader(playerWord.get(), playerRack, true);
            bag.deleteWord(playerWord.get());
            board = board.addWords(playerWord.get());
            board.printBoard();
        }
    }

    private void printMoveHeader(Word word, Rack rack, boolean isPlayer) {
        if (isPlayer) {
            System.out.println("PLAYER MOVE");
        } else {
            System.out.println("OPPONENT MOVE");
        }
        System.out.println("LETTERS: " + rack.print());
        System.out.println("WORD: " + word.stringForm());
        System.out.println("SCORE: " + word.getWordScore());
    }

    @Test
    public void startResponseTest() {

//        Board board = freshScrabbleBoard();
//        System.out.println("START BOARD");
//        board.printBoard();
//        board.updatePossibilities(possibilitiesFactory.generatePossibilities(board.getAnchors(), board));

        for (int i = 0; i < 10; i++) {
            Board board = freshScrabbleBoard();
            board.updatePossibilities(possibilitiesFactory.generatePossibilities(board.getAnchors(), board));
            LetterBag bag = LetterBag.initialBag();
            Rack playerRack = RackUtil.randomRack(bag);
            Rack opponentRack = RackUtil.randomRack(bag);


            Optional<Word> playerWord = simulation.simulateWithoutScore(board.clone(), playerRack, bag);
            if (playerWord.isPresent()) {
                bag.deleteWord(playerWord.get());
                board = board.addWords(playerWord.get());
                board.updatePossibilities(possibilitiesFactory.generatePossibilities(board.getAnchors(), board));
                printMoveHeader(playerWord.get(), playerRack, true);
                board.printBoard();
                playerRack = playerRack.withoutLetters(playerWord.get());
                playerRack = RackUtil.drawLetters(bag, playerRack);
            }


            Optional<Word> opponentWord = simulation.simulateWithoutScore(board.clone(), opponentRack, bag);
            if (opponentWord.isPresent()) {
                bag.deleteWord(opponentWord.get());
                board = board.addWords(opponentWord.get());
                board.updatePossibilities(possibilitiesFactory.generatePossibilities(board.getAnchors(), board));
                printMoveHeader(opponentWord.get(), opponentRack, false);
                board.printBoard();
                opponentRack = opponentRack.withoutLetters(opponentWord.get());
                opponentRack = RackUtil.drawLetters(bag, opponentRack);
            }
        }
    }

    @Test
    public void startThreeMovesTest() {

        for (int i = 0; i < 10; i++) {

            Board board = freshScrabbleBoard();
            board.updatePossibilities(possibilitiesFactory.generatePossibilities(board.getAnchors(), board));

            LetterBag bag = LetterBag.initialBag();
            Rack playerRack = RackUtil.randomRack(bag);
            Rack opponentRack = RackUtil.randomRack(bag);


            Optional<Word> playerWord = simulation.simulateWithoutScore(board.clone(), playerRack, bag);
            if (playerWord.isPresent()) {
                bag.deleteWord(playerWord.get());
                board = board.addWords(playerWord.get());
                board.updatePossibilities(possibilitiesFactory.generatePossibilities(board.getAnchors(), board));
                printMoveHeader(playerWord.get(), playerRack, true);
                board.printBoard();
                playerRack = playerRack.withoutLetters(playerWord.get());
                playerRack = RackUtil.drawLetters(bag, playerRack);
            }


            Optional<Word> opponentWord = simulation.simulateWithoutScore(board.clone(), opponentRack, bag);
            if (opponentWord.isPresent()) {
                bag.deleteWord(opponentWord.get());
                board = board.addWords(opponentWord.get());
                board.updatePossibilities(possibilitiesFactory.generatePossibilities(board.getAnchors(), board));
                printMoveHeader(opponentWord.get(), opponentRack, false);
                board.printBoard();
                opponentRack = opponentRack.withoutLetters(opponentWord.get());
                opponentRack = RackUtil.drawLetters(bag, opponentRack);
            }

            playerWord = simulation.simulateWithoutScore(board.clone(), playerRack, bag);
            if (playerWord.isPresent()) {
                bag.deleteWord(playerWord.get());
                board = board.addWords(playerWord.get());
                board.updatePossibilities(possibilitiesFactory.generatePossibilities(board.getAnchors(), board));
                printMoveHeader(playerWord.get(), playerRack, true);
                board.printBoard();
                playerRack = playerRack.withoutLetters(playerWord.get());
                playerRack = RackUtil.drawLetters(bag, playerRack);
            }
        }
    }

    @Test
    public void startLongTermTest() {

        Board board = freshScrabbleBoard();
        board.updatePossibilities(possibilitiesFactory.generatePossibilities(board.getAnchors(), board));

        LetterBag bag = LetterBag.initialBag();
        Rack playerRack = RackUtil.randomRack(bag);
        Rack opponentRack = RackUtil.randomRack(bag);
        int moveNum = 1;
        for (int i = 0; i < 3; i++) {



            Optional<Word> playerWord = simulation.simulateWithoutScore(board.clone(), playerRack, bag);
            if (playerWord.isPresent()) {
                bag.deleteWord(playerWord.get());
                board = board.addWords(playerWord.get());
                board.updatePossibilities(possibilitiesFactory.generatePossibilities(board.getAnchors(), board));
//                printMoveHeader(playerWord.get(), playerRack, true);
                playerRack = playerRack.withoutLetters(playerWord.get());
                playerRack = RackUtil.drawLetters(bag, playerRack);
            }
            System.out.println("MOVE " + moveNum);
            board.printBoard();
            moveNum++;

            Optional<Word> opponentWord = simulation.simulateWithoutScore(board.clone(), opponentRack, bag);
            if (opponentWord.isPresent()) {
                bag.deleteWord(opponentWord.get());
                board = board.addWords(opponentWord.get());
                board.updatePossibilities(possibilitiesFactory.generatePossibilities(board.getAnchors(), board));
//                printMoveHeader(opponentWord.get(), opponentRack, false);
//                board.printBoard();
                opponentRack = opponentRack.withoutLetters(opponentWord.get());
                opponentRack = RackUtil.drawLetters(bag, opponentRack);
            }
            System.out.println("MOVE " + moveNum);
            board.printBoard();
            moveNum++;

            playerWord = simulation.simulateWithoutScore(board.clone(), playerRack, bag);
            if (playerWord.isPresent()) {
                bag.deleteWord(playerWord.get());
                board = board.addWords(playerWord.get());
                board.updatePossibilities(possibilitiesFactory.generatePossibilities(board.getAnchors(), board));
//                printMoveHeader(playerWord.get(), playerRack, true);
//                board.printBoard();
                playerRack = playerRack.withoutLetters(playerWord.get());
                playerRack = RackUtil.drawLetters(bag, playerRack);
            }
            System.out.println("MOVE " + moveNum);
            board.printBoard();
            moveNum++;
        }
    }
}
