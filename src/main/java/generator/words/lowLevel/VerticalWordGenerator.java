package generator.words.lowLevel;

import board.board.Board;
import board.board.fields.BoardTraverser;
import board.board.fields.Field;
import board.words.Word;
import board.words.Word.WordCandidate;
import dictionary.ScrabbleDictionary;
import dictionary.TrieNode;
import lombok.Builder;
import lombok.Value;
import player.letters.Rack;

import java.util.List;
import java.util.Optional;

import static board.board.fields.PlacementType.VERTICAL;
import static board.words.Word.WordCandidate.wordCandidate;
import static java.lang.Math.min;
import static java.util.Collections.emptyList;
import static util.lists.Lists.modifiableEmptyList;
import static util.logic.LogicalExpressions.not;

@Builder
@Value
public class VerticalWordGenerator implements WordGenerator {

    // space oznacza o ile pol jestesmy na lewo od anchora, 0 oznacza anchora, 1 o jeden w lewo od anchora itd.

    Board board;
    Rack startingRack;
    int anchorRow;
    int anchorCol;
    ScrabbleDictionary dictionary;
    List<WordCandidate> generatedWords;

    public List<WordCandidate> generatedWords() {
        return this.generatedWords;
    }

    public void generate() {
        extendDown(generateUpper());
    }

    private List<WordCandidate> generateUpper() {
        Optional<Field> anchor = board.checkField(anchorRow, anchorCol);
        if (anchor.isEmpty()) {
            return emptyList();
        }

        List<WordCandidate> candidates = modifiableEmptyList();
        TrieNode root = dictionary.start();

        if (canMoveUpperward()) {
            searchUpperCandidates(calculateUpperSpace(), root, candidates);
        } else {
            candidates.add(wordCandidate(takeUpperWord(), startingRack));
        }
        return candidates;
    }

    private Word takeUpperWord() {
        return Word.builder()
                .letters(modifiableEmptyList())
                .type(VERTICAL)
                .start(upperOccupiedWordStart())
                .vectorNo(anchorCol)
                .build();
    }

    private int upperOccupiedWordStart() {
        int row = anchorRow - 1;
        Optional<Field> field = board.checkField(row, anchorCol);
        while (field.isPresent() && not(field.get().isEmpty())) {
            row--;
            field = board.checkField(row, anchorCol);
        }

        return row + 1;
    }

    private Word upperStart(int row) {
        return Word.builder()
                .letters(modifiableEmptyList())
                .type(VERTICAL)
                .start(row)
                .vectorNo(anchorCol)
                .build();
    }

    private int calculateUpperSpace() {
        return min(
                BoardTraverser.getEmptyAboveDistance(board, anchorRow, anchorCol),
                (startingRack.size() - 1));
    }

    private boolean canMoveUpperward() {
        Optional <Field> upperNeighbour = board.checkField(anchorRow - 1, anchorCol);
        return upperNeighbour.isPresent() && upperNeighbour.get().isEmpty();
    }

    private void searchUpperCandidates(int upperSpace, TrieNode node, List<WordCandidate> candidates) {
        for (int i = 0; i <= upperSpace; i++) {
            int start = anchorRow - i;
            Word starter = upperStart(start);
            searchUpperCandidates(node, starter, startingRack, candidates, anchorRow, start, i, start);
        }
    }

    private int neighbourBonus(int row, int col, Character letter) {
        return BoardBonusCalculator.horizontalNeighbourBonus(dictionary, board, letter, row, col);
    }

    private void searchUpperCandidates(
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
                                        populatedField,
                                        neighbourBonus(row, col, letter));

                                Optional<TrieNode> neighbour = node.getNeighbour(letter);


                                neighbour.ifPresent(n -> searchUpperCandidates(n,
                                        extendedWord,
                                        rack.withoutLetter(letter),
                                        candidates,
                                        row + 1,
                                        col,
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

    private void extendDown(List<WordCandidate> upperParts) {
        upperParts.forEach(
                upperPart -> {
                    TrieNode node = dictionary.findWord(upperPart.word());
                    if (not(node.isError())) {
                        extendWord(upperPart, node, upperPart.word().getStart(), anchorCol);
                    }
                }
        );
    }

    private void extendWord(WordCandidate candidate, TrieNode node, int row, int col) {
        if (node.isTerminal() && node.isCorrect() && finishedProperly(row, col, candidate)) {
            this.generatedWords.add(candidate.withScore(startingRack));
        } else {
            if (node.isEndOfWord() && finishedProperly(row, col, candidate)) {
                this.generatedWords.add(candidate.withScore(startingRack));
            }
            extendWordDownward(candidate, node, row, col);
        }
    }

    private boolean finishedProperly(int row, int col, WordCandidate candidate) {
        Optional<Field> downNeighbour = board.checkField(row, col + candidate.word().getLength());
        return downNeighbour.isEmpty() || downNeighbour.get().isEmpty();
    }

    private void extendWordDownward(WordCandidate candidate, TrieNode node, int row, int col) {
        Optional<Field> boardField = board.checkField(row, col);
        boardField.ifPresent(field -> {
            if (field.isEmpty()) {
                candidate.rack().getLetters().stream()
                        .filter(field::isPossibility)
                        .forEach(letter -> {
                    if (node.hasNeighbour(letter)) {
                        Field populatedField = field.withLetter(letter);
                        WordCandidate extendedCandidate = wordCandidate(
                                candidate.word().withFieldAndBonus(populatedField, neighbourBonus(row, col, letter)),
//                                candidate.word().withField(populatedField),
                                candidate.rack().withoutLetter(letter)
                        );
                        TrieNode neighbour = node.getNeighbour(letter).get();

                        extendWord(extendedCandidate, neighbour, row + 1, col);
                    }
                });
            } else {
                if (node.hasNeighbour(field.getValue())) {
                    WordCandidate extendedCandidate = candidate.withField(field);
                    TrieNode neighbour = node.getNeighbour(field.getValue()).get();

                    extendWord(extendedCandidate, neighbour, row + 1, col);
                }
            }
        });
    }
}
