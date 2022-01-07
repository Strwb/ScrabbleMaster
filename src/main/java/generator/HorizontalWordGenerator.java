package generator;

import board.Board;
import board.Field;
import board.Word;
import board.Word.WordCandidate;
import dictionary.ScrabbleDictionary;
import dictionary.TrieNode;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import player.Rack;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import static board.PlacementType.HORIZONTAL;
import static board.Word.WordCandidate.wordCandidate;
import static generator.GeneratorUtil.overrideAttempt;
import static java.lang.Math.min;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static util.lists.Lists.modifiableEmptyList;
import static util.logic.LogicalExpressions.not;

@Builder
@Value
public class HorizontalWordGenerator implements Callable<List<Word>>, WordGenerator {

    // space oznacza o ile pol jestesmy na lewo od anchora, 0 oznacza anchora, 1 o jeden w lewo od anchora itd.

    Board board;
    Rack startingRack;
    int anchorRow;
    int anchorCol;
    ScrabbleDictionary dictionary;
    List<Word> generatedWords;

    @Override
    @SneakyThrows
    public List<Word> call() {
        return generateWords();
    }

    public List<Word> generateWords() {
        generate();
        return generatedWords.stream()
                .filter(
                        word -> not(
                                    word.getLetters().stream()
                                            .noneMatch(Field::isEmpty))
                )
                .filter(overrideAttempt(board))
                .collect(toList());
    }

    private void generate() {
        extendRight(generateLeft());
    }

    private List<WordCandidate> generateLeft() {
        Optional<Field> anchor = board.checkField(anchorRow, anchorCol);
        if (anchor.isEmpty()) {
            return emptyList();
        }

        List<WordCandidate> candidates = modifiableEmptyList();
        TrieNode root = dictionary.getDictionary().getRoot();

        if (canMoveLeftward()) {
            searchLeftCandidates(calculateLeftSpace(), root, candidates);
        } else {
            candidates.add(new WordCandidate(takeLeftWord(), startingRack));
        }
        return candidates;
    }

    private Word takeLeftWord() {
        return Word.builder()
                .letters(modifiableEmptyList())
                .type(HORIZONTAL)
                .start(leftOccupiedWordStart())
                .vectorNo(anchorRow)
                .build();
    }

    private int leftOccupiedWordStart() {
        int col = anchorCol - 1;
        Optional<Field> field = board.checkField(anchorRow, col);
        while (field.isPresent() && not(field.get().isEmpty())) {
            col--;
            field = board.checkField(anchorRow, col);
        }

        return col + 1;
    }

    private Word leftStart(int col) {
        return Word.builder()
                .letters(modifiableEmptyList())
                .type(HORIZONTAL)
                .start(col)
                .vectorNo(anchorRow)
                .build();
    }

    private int calculateLeftSpace() {
        int col = anchorCol - 1;
        int row = anchorRow;
        int space = 0;
        Optional<Field> leftNeighbour = board.checkField(row, col);
        while (leftNeighbour.isPresent()) {
            Field leftField = leftNeighbour.get();
            if (not(leftField.isEmpty())) {
                break;
            }
            space++;
            col--;
            leftNeighbour = board.checkField(row, col);
        }
        int fSpace = min(space, startingRack.size() - 1);
        System.out.println("SPACE RETURNED: " + fSpace);
        return fSpace;
    }

    private boolean canMoveLeftward() {
        Optional <Field> leftNeighbour = board.checkField(anchorRow, anchorCol - 1);
        return leftNeighbour.isPresent() && leftNeighbour.get().isEmpty();
    }

    private void searchLeftCandidates(int leftSpace, TrieNode node, List<WordCandidate> candidates) {
        for (int i = 0; i <= leftSpace; i++) {
            int start = anchorCol - i;
            Word starter = leftStart(start);
            searchLeftCandidates(node, starter, startingRack, candidates, anchorRow, start, i, start);
        }
    }

    private void searchLeftCandidates(
            TrieNode node,
            Word word,
            Rack rack,
            List<WordCandidate> candidates,
            int row,
            int col,
            int space,
            int start) {

        Optional<Field> field = board.checkField(row, col);

        field.ifPresent(fld -> {

                    if (fld.isEmpty()) {


                        rack.getLetters().stream()
                                .filter(fld::isPossibility)
                                .forEach(letter -> {

                            if (node.hasNeighbour(letter)) {

                                Field populatedField = fld.withLetter(letter);

                                Word extendedWord = word.extend(
                                        word.getType(),
                                        word.getVectorNo(),
                                        start,
                                        populatedField);

                                Optional<TrieNode> neighbour = node.getNeighbour(letter);


                                neighbour.ifPresent(n -> searchLeftCandidates(n,
                                        extendedWord,
                                        rack.withoutLetter(letter),
                                        candidates,
                                        row,
                                        col + 1,
                                        space - 1,
                                        start)
                                );
                            }
                        });
                    } else {
                        candidates.add(wordCandidate(word, rack));

                    }
                }
        );
    }

    private void extendRight(List<WordCandidate> leftParts) {
//        leftParts.forEach(System.out::println);
        leftParts.forEach(
                leftPart -> {
                    TrieNode node = dictionary.findWord(leftPart.word());
                    if (not(node.isError())) {
                        extendWord(leftPart, node, anchorRow, leftPart.word().getStart());
                    }
                }
        );
    }

    private void extendWord(WordCandidate candidate, TrieNode node, int row, int col) {
        if (node.isTerminal() && node.isCorrect() && finishedProperly(row, col, candidate)) {
            this.generatedWords.add(candidate.word());
        } else {
            if (node.isEndOfWord() && finishedProperly(row, col, candidate)) {
                this.generatedWords.add(candidate.word());
            }
            extendWordRightward(candidate, node, row, col);
        }
    }

    private boolean finishedProperly(int row, int col, WordCandidate candidate) {
        Optional<Field> rightNeighbour = board.checkField(row, col + candidate.word().getLength());
        return rightNeighbour.isEmpty() || rightNeighbour.get().isEmpty();
    }

    private void extendWordRightward(WordCandidate candidate, TrieNode node, int row, int col) {
        Optional<Field> boardField = board.checkField(row, col);
        boardField.ifPresent(field -> {
            if (field.isEmpty()) {
                candidate.rack().getLetters().stream()
                        .filter(field::isPossibility)
                        .forEach(letter -> {
                    if (node.hasNeighbour(letter)) {
                        Field populatedField = field.withLetter(letter);
                        WordCandidate extendedCandidate = wordCandidate(
                                candidate.word().withField(populatedField),
                                candidate.rack().withoutLetter(letter)
                        );
                        TrieNode neighbour = node.getNeighbour(letter).get();

                        extendWord(extendedCandidate, neighbour, row, col + 1);
                    }
                });
            } else {
                if (node.hasNeighbour(field.getValue())) {
                    WordCandidate extendedCandidate = candidate.withField(field);
                    TrieNode neighbour = node.getNeighbour(field.getValue()).get();

                    extendWord(extendedCandidate, neighbour, row, col + 1);
                }
            }
        });
    }
}
