package simulation;

import board.board.Board;
import board.words.Word;
import generator.possibilities.PossibilitiesFactory;
import generator.words.GenerationFactory;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import player.letters.LetterBag;
import player.letters.Rack;
import player.letters.RackUtil;
import simulation.Simulation.SimulationResult;

import java.util.Optional;
import java.util.concurrent.Callable;

@Builder
class SimulationRunner implements Callable<SimulationResult> {

    Word original;
    Rack playerRack;
    Board board;
    LetterBag bag;
    GenerationFactory generationFactory;
    PossibilitiesFactory possibilitiesFactory;
    int iterations;

    @Override
    @SneakyThrows
    public SimulationResult call() {
        double score = 0;

        for (int i = 0; i < iterations; i++) {
            SimulationRun run = new SimulationRun();
            score += run.simulate(original, playerRack, board, bag);
        }

        return new SimulationResult(original, (score / iterations));
    }

    @Value
    private class SimulationRun {

        Integer simulate(
                Word original,
                Rack playerRack,
                Board board,
                LetterBag bag
        ) {

            int opponentScore;
            int longTermScore;

            board = board.addWords(original);
            updatePossibilities(board);

            playerRack = updatePlayerRack(bag, playerRack);
            Rack opponentRack = randomizeOpponentRack(bag);
            Optional<Word> opponentWord = findOpponentMove(opponentRack, board);

            opponentScore = opponentWord.map(Word::getWordScore).orElse(0);

            if (opponentWord.isPresent()) {
                board = board.addWords(opponentWord.get());
                updatePossibilities(board);
            }

            Optional<Word> simulationWord = findFinalPlayerWord(playerRack, board);

            longTermScore = simulationWord.map(Word::getWordScore).orElse(0);

            if (simulationWord.isPresent()) {
                board = board.addWords(simulationWord.get());
                updatePossibilities(board);
            }

            return simulationScore(original.getWordScore(), opponentScore, longTermScore);
        }

        private Rack updatePlayerRack(LetterBag bag, Rack playerRack) {
            return RackUtil.drawLetters(bag, playerRack);
        }

        private Rack randomizeOpponentRack(LetterBag bag) {
            return RackUtil.randomRack(bag);
        }

        private Optional<Word> findOpponentMove(Rack opponentRack, Board board) {
            return generationFactory.findNextMove(board, opponentRack, board.getAnchors());
        }

        private void updatePossibilities(Board board) {
            board.updatePossibilities(possibilitiesFactory.generatePossibilities(board.getAnchors(), board));
        }

        private Optional<Word> findFinalPlayerWord(Rack playerRack, Board board) {
            return generationFactory.findNextMove(board, playerRack, board.getAnchors());
        }

        private int simulationScore(int original, int opponent, int simulated) {
            return original - opponent + simulated;
        }

    }

}
