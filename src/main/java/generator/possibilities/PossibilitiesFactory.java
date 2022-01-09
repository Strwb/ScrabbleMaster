package generator.possibilities;

import board.board.Board;
import board.board.Board.Anchor;
import board.board.Coordinate;
import board.board.fields.Field;
import board.words.Word;
import dictionary.ScrabbleDictionary;
import dictionary.TrieNode;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import static board.board.fields.PlacementType.HORIZONTAL;
import static board.board.fields.PlacementType.VERTICAL;
import static board.words.WordUtil.wholeAlphabet;
import static com.google.common.collect.Sets.intersection;
import static generator.possibilities.PossibilitiesFactory.Traversal.*;
import static java.util.stream.Collectors.toSet;
import static util.lists.Lists.modifiableEmptyList;
import static util.logic.LogicalExpressions.not;

public class PossibilitiesFactory {

    ScrabbleDictionary dictionary;

    public PossibilitiesFactory(ScrabbleDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Set<StampedAnchor> generatePossibilities(Set<Anchor> anchors, Board board) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        List<PossibilitiesGenerator> tasks = anchors.stream()
                .map(Coordinate::fromAnchor)
                .map(coordinate -> new PossibilitiesGenerator(dictionary, coordinate, board))
                .toList();

        System.out.println("NUMBER OF POSSIBILTY TASKS: " + tasks.size());
        List<Future<StampedAnchor>> results;

        try {
            results = executorService.invokeAll(tasks);

        } catch (InterruptedException e) {
            throw new RuntimeException("Error during horizontal generation");
        }

        return results.stream()
                .map(extractFutureData())
                .flatMap(Optional::stream)
                .collect(toSet());
    }

    private Function<Future<StampedAnchor>, Optional<StampedAnchor>> extractFutureData() {
        return future -> {
            try {
                return Optional.of(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        };
    }

    private Function<Coordinate, StampedAnchor> stamp(Board board) {
        return coordinate -> new StampedAnchor(
                coordinate.row(),
                coordinate.col(),
                calculatePossibilities(coordinate, board)
        );
    }

    private Set<Character> calculatePossibilities(Coordinate coordinate, Board board) {
        Set<Character> verticalIntersection = intersection(
                up(coordinate, board),
                down(coordinate, board)
        );

        Set<Character> horizontalIntersection = intersection(
                left(coordinate, board),
                right(coordinate, board)
        ) ;

        return intersection(
                verticalIntersection,
                horizontalIntersection
        );
    }

    private Set<Character> up(Coordinate coordinate, Board board) {
        int row = coordinate.row() - 1;
        int col = coordinate.col();
        Optional<Field> up = board.checkField(row, col);

        if (up.isPresent() && not(up.get().isEmpty())) {
            return possibilitiesUp(coordinate, board);
        }

        return wholeAlphabet();
    }

    private Set<Character> down(Coordinate coordinate, Board board) {
        int row = coordinate.row() + 1;
        int col = coordinate.col();
        Optional<Field> down = board.checkField(row, col);

        if (down.isPresent() && not(down.get().isEmpty())) {
            return possibilitiesDown(coordinate, board);
        }

        return wholeAlphabet();
    }

    private Set<Character> left(Coordinate coordinate, Board board) {
        int row = coordinate.row();
        int col = coordinate.col() - 1;
        Optional<Field> left = board.checkField(row, col);

        if (left.isPresent() && not(left.get().isEmpty())) {
            return possibilitiesLeft(coordinate, board);
        }

        return wholeAlphabet();
    }

    private Set<Character> right(Coordinate coordinate, Board board) {
        int row = coordinate.row();
        int col = coordinate.col() + 1;
        Optional<Field> right = board.checkField(row, col);

        if (right.isPresent() && not(right.get().isEmpty())) {
            return possibilitiesRight(coordinate, board);
        }

        return wholeAlphabet();
    }

    private Set<Character> possibilitiesUp(Coordinate target, Board board) {
        Coordinate wordBeginning = goAllTheWayUp(target, board);
        return traverseDown(wordBeginning, target, board);
    }

    private Set<Character> traverseDown(
            Coordinate wordStart,
            Coordinate targetField,
            Board board) {
        // - isc na sam koniec do gory DONE
        // - zapisac sobie slowo od samej gory do pola powyzej coordinate
        // - znalezc to slowo w slowniku
        // - sprawdzic jakich sasiadow ma koncowka tego slowa

        int row = wordStart.row();
        List<Field> fields = modifiableEmptyList();

        while (row < targetField.row()) {
            fields.add(board.checkField(row, wordStart.col()).get());
            row++;
        }

        Word word = Word.builder()
                .type(VERTICAL)
                .vectorNo(wordStart.col())
                .start(wordStart.row())
                .letters(fields)
                .build();

        TrieNode endNode = dictionary.findWord(word);

        return endNode.getNeighbours().keySet();
    }

    private Coordinate goAllTheWayUp(Coordinate coordinate, Board board) {
        int row = coordinate.row();
        int col = coordinate.col();

        Optional<Field> field = board.checkField(row, col);

        while (not(field.isEmpty()) && not(field.get().isEmpty())) {
            row--;
            field = board.checkField(row, col);
        }
        return new Coordinate(row + 1, col);
    }

    private Set<Character> possibilitiesLeft(Coordinate target, Board board) {
        Coordinate wordStart = goAllTheWayLeft(target, board);
        return traverseDown(wordStart, target, board);
    }

    private Set<Character> traverseRight(
            Coordinate wordStart,
            Coordinate targetField,
            Board board) {

        int col = wordStart.col();
        List<Field> letters = modifiableEmptyList();

        while (col < targetField.col()) {
            letters.add(board.checkField(wordStart.row(), col).get());
            col++;
        }

        Word word = Word.builder()
                .type(HORIZONTAL)
                .vectorNo(wordStart.row())
                .start(wordStart.col())
                .letters(letters)
                .build();

        TrieNode endNode = dictionary.findWord(word);

        return endNode.getNeighbours().keySet();
    }

    private Coordinate goAllTheWayLeft(Coordinate coordinate, Board board) {
        int row = coordinate.row();
        int col = coordinate.col();

        Optional<Field> field = board.checkField(row, col);

        while (not(field.isEmpty()) && not(field.get().isEmpty())) {
            col--;
            field = board.checkField(row, col);
        }
        return new Coordinate(row + 1, col);
    }

    private Set<Character> possibilitiesDown(Coordinate coordinate, Board board) {
        int difference = 1;

        int row = coordinate.row() + difference;
        Optional<Field> field = board.checkField(row, coordinate.col());

        if (field.isEmpty() || field.get().isEmpty()) {
            return wholeAlphabet();
        }

        Set<Traversal> traversals = initialTraversals(dictionary.getDictionary().getRoot());

        while (field.isPresent() && not(field.get().isEmpty())) {
            traversals = findNeighbours(traversals, field.get());
            row += difference;
            field = board.checkField(row, coordinate.col());
        }

        return toCharacterSet(traversals);
    }

    private Set<Character> possibilitiesRight(Coordinate coordinate, Board board) {
        int difference = 1;

        int col = coordinate.col() + difference;
        Optional<Field> field = board.checkField(coordinate.row(), col);

        if (field.isEmpty() || field.get().isEmpty()) {
            return wholeAlphabet();
        }

        Set<Traversal> traversals = initialTraversals(dictionary.getDictionary().getRoot());

        while (field.isPresent() && not(field.get().isEmpty())) {
            traversals = findNeighbours(traversals, field.get());
            col += difference;
            field = board.checkField(coordinate.row(), col);
        }

        return toCharacterSet(traversals);
    }

    record Traversal(String path, TrieNode node, Character start) {

        static Set<Traversal> findNeighbours(Set<Traversal> traversals, Field field) {
            if (field.isEmpty()) {
                return traversals;
            }
            Character fieldValue = field.getValue();
            return traversals.stream()
                    .filter(traversal -> traversal.node.hasNeighbour(fieldValue))
                    .map(traversal -> new Traversal(
                            traversal.path + fieldValue,
                            traversal.node.getNeighbour(fieldValue).get(),
                            traversal.start))
                    .collect(toSet());
        }

        static Set<Traversal> initialTraversals(TrieNode root) {
            return wholeAlphabet().stream()
                    .filter(root::hasNeighbour)
                    .map(root::getNeighbour)
                    .flatMap(Optional::stream)
                    .map(node -> new Traversal(
                            String.valueOf(node.getLetter()),
                            node,
                            node.getLetter()))
                    .collect(toSet());
        }

        static Set<Character> toCharacterSet(Set<Traversal> traversals) {
            return traversals.stream()
                    .map(Traversal::start)
                    .collect(toSet());
        }
    };

    public record StampedAnchor(int row, int col, Set<Character> possibilities){};
}
