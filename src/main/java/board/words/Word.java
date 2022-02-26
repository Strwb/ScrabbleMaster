package board.words;

import board.board.fields.Field;
import board.board.fields.PlacementType;
import generator.words.lowLevel.WordBonusCalculator;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import player.letters.Rack;

import java.util.List;

import static util.lists.Lists.modifiableCopyOf;

@Value
@Builder
@With
public class Word {

    PlacementType type;
    int vectorNo;
    int start;
    List<Field> letters;
    @With
    @Builder.Default
    int scoreBonus = 0; // neighbour bonus + bingo bonus

    public Word withField(Field field) {
        var expanded = modifiableCopyOf(letters);
        expanded.add(field);
        return this
                .withLetters(expanded);
    }

    public Word withFieldAndBonus(Field field, int neighbourBonus) {
        var expanded = modifiableCopyOf(letters);
        expanded.add(field);
        return Word.builder()
                .type(type)
                .vectorNo(vectorNo)
                .start(start)
                .letters(expanded)
                .scoreBonus(this.scoreBonus + neighbourBonus)
                .build();
    }

    private Word withCandidateBonus(int candidateBonus) {
        return this.withScoreBonus(this.scoreBonus + candidateBonus);
    }

    public int getWordScore() {
        return WordUtil.wordScore(letters) + scoreBonus;
    }

    private int getScore() {
        return WordUtil.calculateWordScore(letters) + scoreBonus;
    }

    public String stringForm() {
        return WordUtil.stringForm(letters);
//        StringBuilder sb = new StringBuilder(letters.size());
//        letters.forEach(letter -> sb.append(letter.getValue()));
//        return sb.toString();
    }

    public Word extend(PlacementType type, int vectorNo, int start, Field letter, int scoreBonus) {
        List<Field> letters = modifiableCopyOf(this.letters);
        letters.add(letter);
        return Word.builder()
                .type(type)
                .vectorNo(vectorNo)
                .start(start)
                .letters(letters)
                .scoreBonus(this.scoreBonus + scoreBonus)
                .build();
    }

    public int getLength() {
        return letters.size();
    }

    public record WordCandidate(Word word, Rack rack, int scoreBonus) {

        public Word extractWord() {
            return this.word.withCandidateBonus(scoreBonus);
        }

        public WordCandidate withField(Field field) {
            return new WordCandidate(
                    word.withField(field),
                    rack.withoutLetter(field.getValue()),
                    0);
        }

        public int getScore() {
            return word().getScore() + scoreBonus;
        }

        public WordCandidate withScore(Rack startingRack) {
            return copyWithScore(WordBonusCalculator.calculateBonus(this, startingRack));
        }

        private WordCandidate copyWithScore(int score) {
            return new WordCandidate(
                    word,
                    rack,
                    score
            );
        }

        public static WordCandidate wordCandidate(Word word, Rack rack) {
            return new WordCandidate(word, rack, 0);
        }

        public static WordCandidate wordCandidate(Word word, Rack rack, int scoreBonus) {
            return new WordCandidate(word, rack, scoreBonus);
        }
    }
}
