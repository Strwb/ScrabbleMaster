package generator.words;

import board.board.Board;
import board.board.Board.Anchor;
import board.words.Word;
import board.words.Word.WordCandidate;
import dictionary.ScrabbleDictionary;
import generator.words.lowLevel.SideGenerator;
import lombok.experimental.FieldDefaults;
import player.letters.Rack;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static generator.words.GeneratorUtil.*;
import static lombok.AccessLevel.PRIVATE;
import static util.lists.Lists.modifiableEmptyList;

@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GenerationFactory {

    ScrabbleDictionary dictionary;

    public GenerationFactory(ScrabbleDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Optional<Word> findNextMove(Board board, Rack rack, Set<Anchor> anchors) {
        Optional<Word> maxWord = generateMove(board, rack, anchors);

        return maxWord;
    }

    public List<Word> findPossibleMoves(Board board, Rack rack, Set<Anchor> anchors) {
        return findBestMove(board, rack, anchors);
    }

    // sam ruch -> 40ms, 52, 39, 49, 45 (4 watki)
    // sam ruch ->  64, 42, 42, 41 (1 watek)
    private Optional<Word> generateMove(Board board, Rack rack, Set<Anchor> anchors) {
        return GeneratorUtil.getMaxWord(findBestMove(board, rack, anchors));
    }

    private List<Word> findBestMove(Board board, Rack rack, Set<Anchor> anchors) {
        List<SideGenerator.Generator> results = modifiableEmptyList();
        SideGenerator generator = new SideGenerator(board, rack, dictionary);
        anchors.forEach(anchor -> {
            results.add(generator.generate(anchor));
        });

        List<WordCandidate> sorted = GeneratorUtil.getGeneratedWords(results).stream()
                .sorted(Comparator.comparing(WordCandidate::getScore).reversed())
                .toList();

        return highScoringWord(sorted);
    }
}
