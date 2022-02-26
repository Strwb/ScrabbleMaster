package board.board.fields;

import dictionary.Score;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.With;

import java.io.Serializable;
import java.util.Set;

import static board.board.fields.FieldBonus.*;
import static board.words.WordUtil.noPossibilites;
import static board.words.WordUtil.wholeAlphabet;

@Builder
@Value
@With
public class Field implements Serializable {

    Character value;
    FieldBonus bonus;
    boolean empty;
    Set<Character> possibilities;

    public boolean isPossibility(Character value) {
        return possibilities.contains(value);
    }

    public void setPossibilities(Set<Character> possibilities) {
        this.withPossibilities(possibilities); //FIXME
    }

    public Field withLetter(Character letter) {
        return this.withValue(letter);
    }

    public boolean isPresent() {
        return !empty;
    }

    public int letterValue() {
        return empty ?
                0 :
                Score.checkLetterValue(value);
    }

    @SneakyThrows
    public Field clone() {
        return (Field) super.clone();
    }

    public static Field emptyField() {
        return Field.builder()
                .value('/')
                .bonus(NONE)
                .empty(true)
                .possibilities((wholeAlphabet()))
                .build();
    }

    public static Field doubleLetterField() {
        return Field.builder()
                .value('/')
                .bonus(DOUBLE_LETTER)
                .empty(true)
                .possibilities(((wholeAlphabet())))
                .build();
    }

    public static Field tripleLetterField() {
        return Field.builder()
                .value('/')
                .bonus(TRIPLE_LETTER)
                .empty(true)
                .possibilities(((wholeAlphabet())))
                .build();
    }

    public static Field doubleWordField() {
        return Field.builder()
                .value('/')
                .bonus(DOUBLE_WORD)
                .empty(true)
                .possibilities(((wholeAlphabet())))
                .build();
    }

    public static Field tripleWordField() {
        return Field.builder()
                .value('/')
                .bonus(TRIPLE_WORD)
                .empty(true)
                .possibilities(((wholeAlphabet())))
                .build();
    }

    public static Field assignField(Character value) {
        return Field.builder()
                .value(value)
                .bonus(NONE)
                .empty(false)
                .possibilities(((noPossibilites())))
                .build();
    }


}
