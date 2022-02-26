package simulation;

import board.board.Board;
import board.words.Word;
import dictionary.ScrabbleDictionary;
import generator.possibilities.PossibilitiesFactory;
import generator.words.GenerationFactory;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import player.letters.LetterBag;
import player.letters.Rack;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class Simulation {

    // No multithreading - over 9s
    // Multithreading - 4.4s

    GenerationFactory generationFactory;
    PossibilitiesFactory possibilitiesFactory;

    public Simulation(ScrabbleDictionary dictionary) {
        dictionary.loadDictionary();
        generationFactory = new GenerationFactory(dictionary);
        possibilitiesFactory = new PossibilitiesFactory(dictionary);
    }

    public Optional<Word> simulate(Board board, Rack playerRack) {
        LetterBag bag = LetterBag.initialBag();
        bag = bag.deleteUsedLetters(board, playerRack);
        List<Word> wordCandidates = playerWordCandidates(board, playerRack);


        List<SimulationResult> simulationResults = performSimulation(wordCandidates, board, playerRack, bag);

        Optional<SimulationResult> bestResult = simulationResults.stream()
                .max(Comparator.comparing(SimulationResult::score));

        return bestResult.map(SimulationResult::word);
    }

    List<SimulationResult> performSimulation(List<Word> words, Board board, Rack playerRack, LetterBag bag) {
        List<SimulationRunner> runs = words.stream()
                .map(word -> perWordSimulation(word, board, playerRack, bag))
                .toList();

        return execute(runs);
    }

    @SneakyThrows
    List<SimulationResult> execute(List<SimulationRunner> runs) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        List<SimulationResult> results = executeRuns(runs, executorService);

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        return results;
    }

    List<SimulationResult> executeRuns(List<SimulationRunner> runs, ExecutorService executorService) {
        List<Future<SimulationResult>> results;
        try {
            results = executorService.invokeAll(runs);
        } catch (InterruptedException e) {
            throw new RuntimeException("Error during generation");
        }

        return results.stream()
                .map(this::extractResults)
                .filter(Optional::isPresent)
                .flatMap(Optional::stream)
                .toList();
    }

    Optional<SimulationResult> extractResults(Future<SimulationResult> future) {
        SimulationResult result = null;
        try {
            result = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(result);
    }

    SimulationRunner perWordSimulation(Word word, Board board, Rack playerRack, LetterBag bag) {
        return SimulationRunner.builder()
                .bag(bag)
                .board(board)
                .original(word)
                .playerRack(playerRack)
                .generationFactory(generationFactory)
                .possibilitiesFactory(possibilitiesFactory)
                .iterations(50)
                .build();
    }

    List<Word> playerWordCandidates(Board board, Rack playerRack) {
        return generationFactory.findPossibleMoves(board, playerRack, board.getAnchors());
    }

    record SimulationResult(Word word, Double score){}
}
