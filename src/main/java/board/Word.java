package board;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import player.Rack;

import java.util.List;

import static board.WordUtil.calculateWordScore;
import static util.lists.Lists.modifiableCopyOf;
import static util.lists.Lists.modifiableEmptyList;

@Value
@Builder
@With
public class Word {

    PlacementType type;
    int vectorNo;
    int start;
    List<Field> letters;
    @Builder.Default
    List<Field> freshLetters = modifiableEmptyList();

    public Word withField(Field field) {
        var expanded = modifiableCopyOf(letters);
        expanded.add(field);
        return this
                .withLetters(expanded);
    }

    public int getScore() {
        return calculateWordScore(letters);
    }

    public String stringForm() {
        StringBuilder sb = new StringBuilder(letters.size());
        letters.forEach(letter -> sb.append(letter.getValue()));
        return sb.toString();
    }

    public Word wordWithLetter(PlacementType type, int vectorNo, int start, Field letter) {
        List<Field> letters = modifiableCopyOf(this.letters);
        letters.add(letter);
        return Word.builder()
                .type(type)
                .vectorNo(vectorNo)
                .start(start)
                .letters(letters)
                .build();
    }

    public int getLength() {
        return letters.size();
    }

    public record WordCandidate(Word word, Rack rack) {
        public WordCandidate withField(Field field) {
            return new WordCandidate(
                    word.withField(field),
                    rack.withoutLetter(field.getValue()));
        }

        public static WordCandidate wordCandidate(Word word, Rack rack) {
            return new WordCandidate(word, rack);
        }
    }
}
