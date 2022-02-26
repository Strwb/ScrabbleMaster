package simulation;

import board.board.Board;
import board.words.Word;
import dictionary.ScrabbleDictionary;
import generator.possibilities.PossibilitiesFactory;
import generator.words.GenerationFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import player.letters.LetterBag;
import player.letters.Rack;
import player.letters.RackUtil;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static lombok.AccessLevel.PRIVATE;
import static util.lists.Lists.modifiableEmptyList;

@Value
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class Simulation {

    // No multithreading - 1min 47s
    // Multithreading - 1min 33s

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


        Map<Word, Double> simulationResults = performSimulation(wordCandidates, board, playerRack, bag);
        Optional<Map.Entry<Word, Double>> bestWord = simulationResults.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        return bestWord.map(Map.Entry::getKey);
    }

    Map<Word, Double> performSimulation(List<Word> words, Board board, Rack playerRack, LetterBag bag) {
        Map<Word, List<SimulationRun>> runs = words.stream()
                .map(word -> perWordSimulation(word, board, playerRack, bag))
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(SimulationRun::getOriginal));

        return execute(runs);
    }

    Map<Word, Double> execute(Map<Word, List<SimulationRun>> runs) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        return runs.entrySet().stream()
                .map(entry -> new SimpleEntry<Word, Double>(
                        entry.getKey(),
                        executeBatch(entry.getValue(), executorService)))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

    Double executeBatch (List<SimulationRun> batch, ExecutorService executorService) {
        List<Future<Integer>> results;
        try {
            results = executorService.invokeAll(batch);
        } catch (InterruptedException e) {
            throw new RuntimeException("Error during horizontal generation");
        }
        OptionalDouble result = extractResult(results);

        return result.isPresent() ?
                result.getAsDouble() :
                0;
    }

    OptionalDouble extractResult(List<Future<Integer>> results) {
        List<Integer> runResults = modifiableEmptyList();
        results.forEach(future -> {
            try {
                runResults.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        return runResults.stream()
                .mapToDouble(result -> result)
                .average();
    }

    //TODO:
    // - Premia 50 punktow za uzycie calej tacki
    // - Sprawdzic inne zasady ktore mozna uzyc
    // - Blank letter
    // - Score existing words

    List<SimulationRun> perWordSimulation(Word word, Board board, Rack playerRack, LetterBag bag) {
        List<SimulationRun> runs = modifiableEmptyList();
        for (int i = 0; i < 8; i++) {
            runs.add(
                    SimulationRun.builder()
                            .bag(bag)
                            .board(board)
                            .original(word)
                            .playerRack(playerRack)
                            .generationFactory(generationFactory)
                            .possibilitiesFactory(possibilitiesFactory)
                            .build()
            );
        }
        return runs;
    }

    List<Word> playerWordCandidates(Board board, Rack playerRack) {
        return generationFactory.findPossibleMoves(board, playerRack, board.getAnchors());
    }


    Optional<Word> opponentBestWord(Board board, Rack opponentRack) {
        return generationFactory.findNextMove(board, opponentRack, board.getAnchors());
    }

    @Builder
    private static class SimulationRun implements Callable<Integer> {

        Word original;
        Rack playerRack;
        Board board;
        LetterBag bag;
        GenerationFactory generationFactory;
        PossibilitiesFactory possibilitiesFactory;

        @Override
        @SneakyThrows
        public Integer call() {

            int opponentScore;
            int simulationScore;

            board = board.addWords(original);
            updatePossibilities(board);

            playerRack = updatePlayerRack();
            Rack opponentRack = randomizeOpponentRack();
            Optional<Word> opponentWord = findOpponentMove(opponentRack);

            opponentScore = opponentWord.map(Word::getWordScore).orElse(0);

            if (opponentWord.isPresent()) {
                board = placeWord(opponentWord.get());
                updatePossibilities(board);
            }

            Optional<Word> simulationWord = findFinalPlayerWord(playerRack);

            simulationScore = simulationWord.map(Word::getWordScore).orElse(0);

            if (simulationWord.isPresent()) {
                board = placeWord(simulationWord.get());
                updatePossibilities(board);
            }

//            System.out.println(simulationScore(original.getWordScore(), opponentScore, simulationScore));
            return simulationScore(original.getWordScore(), opponentScore, simulationScore);
        }

        public Word getOriginal() {
            return original;
        }

        private Rack updatePlayerRack() {
            return RackUtil.drawLetters(bag, playerRack);
        }

        private Rack randomizeOpponentRack() {
            return RackUtil.randomRack(bag);
        }

        private Optional<Word> findOpponentMove(Rack opponentRack) {
            return generationFactory.findNextMove(board, opponentRack, board.getAnchors());
        }

        private Board placeWord(Word word) {
            return board.addWords(word);
        }

        private void updatePossibilities(Board board) {
            board.updatePossibilities(possibilitiesFactory.generatePossibilities(board.getAnchors(), board));
        }

        private Optional<Word> findFinalPlayerWord(Rack playerRack) {
            return generationFactory.findNextMove(board, playerRack, board.getAnchors());
        }

        private int simulationScore(int original, int opponent, int simulated) {
            return original - opponent + simulated;
        }
    }
}
