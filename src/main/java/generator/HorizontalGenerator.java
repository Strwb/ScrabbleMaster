package generator;

import board.Board;
import board.Field;
import board.PlacementType;
import board.Word;
import board.Word.WordCandidate;
import dictionary.ScrabbleDictionary;
import dictionary.TrieNode;
import lombok.Builder;
import player.Rack;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import static board.PlacementType.HORIZONTAL;
import static board.Word.WordCandidate.wordCandidate;
import static java.util.Collections.emptyList;
import static util.lists.Lists.modifiableEmptyList;

@Builder
public class HorizontalGenerator implements Callable<List<Word>> {

    Board board;
    Rack startingRack;
    int anchorRow;
    int anchorCol;
    int limit;
    ScrabbleDictionary dictionary;
    List<Word> words;
    PlacementType type;

    @Override
    public List<Word> call() throws Exception {
        generateWords();
        return getResults();
    }

    public List<Word> getResults() {
        return words;
    }

    private void generateWords() {
        extendRight(generateLeft(dictionary.getDictionary().getRoot()));
    }

    private List<WordCandidate> generateLeft(TrieNode node) { //TODO add if there are already words
        Optional<Field> anchor = board.checkField(anchorRow, anchorCol);
        if (anchor.isPresent()) {
            Word word = Word.builder()
                    .letters(modifiableEmptyList())
                    .type(HORIZONTAL)
                    .start(anchorCol)
                    .vectorNo(anchorRow)
                    .build();
            List<WordCandidate> generatedWords = modifiableEmptyList();
            generatedWords.add(new WordCandidate(word, startingRack));
            TrieNode root = dictionary.getDictionary().getRoot();
            searchLeftCandidates(root, word, startingRack, limit, generatedWords, anchorRow, anchorCol - 1);
            return generatedWords;
        }
        return emptyList();
    }

    private void searchLeftCandidates(TrieNode node, Word word, Rack rack, int limit, List<WordCandidate> words, int row, int col) {
        //TODO -> Dodac warunek na possibilities
        Optional<Field> field = board.checkField(row, col);

        field.ifPresent(fld -> {

            if (fld.isEmpty()) {

                rack.getLetters().forEach(letter -> {

                    if (node.hasNeighbour(letter)) {

                        Field populatedField = fld.withLetter(letter);

                        Word extendedWord = word.wordWithLetter(
                                word.getType(),
                                word.getVectorNo(),
                                col,
                                populatedField);

                        Optional<TrieNode> neighbour = node.getNeighbour(letter);
                        words.add(wordCandidate(word, rack.withoutLetter(letter)));

                        if (limit > 1) {

                            neighbour.ifPresent(n -> searchLeftCandidates(n,
                                    extendedWord,
                                    rack.withoutLetter(letter),
                                    limit - 1,
                                    words,
                                    row,
                                    col - 1)
                            );
                        }
                    }
                });
            }}
        );
    }

    private void extendRight(List<WordCandidate> leftParts) {
        leftParts.forEach(
                leftPart -> {
                    TrieNode node = dictionary.findWord(leftPart.word());
                    extendWord(leftPart, node, anchorRow, anchorCol);
                }
        );
    }

    private void extendWord(WordCandidate candidate, TrieNode node, int row, int col) {
        if (node.isTerminal()) {
            this.words.add(candidate.word());
        } else {
            extendWordRightward(candidate, node, row, col);
        }
    }

    private void extendWordRightward(WordCandidate candidate, TrieNode node, int row, int col) {
        Optional<Field> boardField = board.checkField(row, col);
        boardField.ifPresent(field -> {
            if (field.isEmpty()) {
                candidate.rack().getLetters().forEach(letter -> {
                    if (node.hasNeighbour(letter)) {
                        Field populatedField = field.withLetter(letter);
                        WordCandidate extendedCandidate = candidate.withField(populatedField);
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
