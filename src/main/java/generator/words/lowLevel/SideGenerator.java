package generator.words.lowLevel;

import board.board.Board;
import board.board.Board.Anchor;
import board.board.fields.Field;
import board.words.Word.WordCandidate;
import dictionary.ScrabbleDictionary;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import player.letters.Rack;

import java.util.List;

import static generator.words.GeneratorUtil.notOverride;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;
import static util.lists.Lists.modifiableEmptyList;
import static util.logic.LogicalExpressions.not;

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class SideGenerator {

    Board board;
    Rack rack;
    ScrabbleDictionary dictionary;

    public Generator generate(Anchor anchor) {
        return switch (anchor.type()) {
            case VERTICAL -> vertical(anchor);
            case HORIZONTAL -> horizontal(anchor);
        };
    }

    private Generator vertical(Anchor anchor) {
        return new Generator(verticalGenerator(anchor), board);
    }

    private Generator horizontal(Anchor anchor) {
        return new Generator(horizontalGenerator(anchor), board);
    }

    private VerticalWordGenerator verticalGenerator(Anchor anchor) {
        return VerticalWordGenerator.builder()
                .dictionary(dictionary)
                .anchorRow(anchor.row())
                .anchorCol(anchor.col())
                .board(board)
                .startingRack(rack)
                .generatedWords(modifiableEmptyList())
                .build();
    }

    private HorizontalWordGenerator horizontalGenerator(Anchor anchor) {
        return HorizontalWordGenerator.builder()
                .dictionary(dictionary)
                .anchorRow(anchor.row())
                .anchorCol(anchor.col())
                .board(board)
                .startingRack(rack)
                .generatedWords(modifiableEmptyList())
                .build();
    }

    public record Generator(WordGenerator generator, Board board) {

        public List<WordCandidate> generateWords() {
            generator.generate();
            return generator.generatedWords().stream()
                    .filter(
                            candidate -> not(
                                    candidate.word().getLetters().stream()
                                            .noneMatch(Field::isEmpty))
                    )
                    .filter(candidate -> notOverride(board).test(candidate.word()))
                    .collect(toList());
        }
    }

}
