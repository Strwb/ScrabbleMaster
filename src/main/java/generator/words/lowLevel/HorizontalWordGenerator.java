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

import static board.board.fields.PlacementType.HORIZONTAL;
import static board.words.Word.WordCandidate.wordCandidate;
import static java.lang.Math.min;
import static java.util.Collections.emptyList;
import static util.lists.Lists.modifiableEmptyList;
import static util.logic.LogicalExpressions.not;

@Builder
@Value
public class HorizontalWordGenerator implements WordGenerator {

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
        extendRight(generateLeft());
    }

    private List<WordCandidate> generateLeft() {
        Optional<Field> anchor = board.checkField(anchorRow, anchorCol);
        if (anchor.isEmpty()) {
            return emptyList();
        }

        List<WordCandidate> candidates = modifiableEmptyList();
        TrieNode root = dictionary.start();

        if (canMoveLeftward()) {
            searchLeftCandidates(calculateLeftSpace(), root, candidates);
        } else {
//            candidates.add(new WordCandidate(takeLeftWord(), startingRack, 0)); //TODO add bonus here (50 if cleared 7 letter starting rack)
            candidates.add(wordCandidate(takeLeftWord(), startingRack)); //TODO add bonus here (50 if cleared 7 letter starting rack)
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
        return min(
                BoardTraverser.getEmptyLeftDistance(board, anchorRow, anchorCol),
                (startingRack.size() - 1));
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

    private int neighbourBonus(int row, int col, Character letter) {
        return BoardBonusCalculator.verticalNeighbourBonus(dictionary, board, letter, row, col);
    }

    //TODO Tu dac dodawanie
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

                                Word extendedWord = word.extend( // przy kazdym dodawaniu literki do word
                                        word.getType(),
                                        word.getVectorNo(),
                                        start,
                                        populatedField,
                                        neighbourBonus(row, col, letter)
                                );

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
        leftParts.forEach(
                leftPart -> {
                    TrieNode node = dictionary.findWord(leftPart.word());
                    if (not(node.isError())) {
                        extendWord(leftPart, node, anchorRow, leftPart.word().getStart());
                    }
                }
        );
    }

    //TODO -> Refactor, wywalic to isTerminal
    private void extendWord(WordCandidate candidate, TrieNode node, int row, int col) {
        if (node.isTerminal() && node.isCorrect() && finishedProperly(row, col, candidate)) {
            this.generatedWords.add(candidate.withScore(startingRack));
        } else {
            if (node.isEndOfWord() && finishedProperly(row, col, candidate)) {
                this.generatedWords.add(candidate.withScore((startingRack)));
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
                        WordCandidate extendedCandidate = wordCandidate( // przy kazdym rozszerzaniu kandydata
//                                candidate.word().withField(populatedField),
                                candidate.word().withFieldAndBonus(populatedField, neighbourBonus(row, col, letter)),
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
