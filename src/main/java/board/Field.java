package board;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.Set;

import static board.FieldBonus.*;
import static board.WordUtil.noPossibilites;
import static board.WordUtil.wholeAlphabet;

@Builder
@Value
@With
public class Field {

    Character value;
    FieldBonus bonus;
    boolean empty;
    Set<Character> possibilities;

    public boolean isPossibility(Character value) {
        return possibilities.contains(value);
    }

    public void addPossibility(Character value) {
        possibilities.add(value);
    }

    public void removePossibility(Character value) {
        possibilities.remove(value);
    }

    public Field setPossibilities(Set<Character> possibilities) {
        return this.withPossibilities(possibilities);
    }

    public Field withLetter(Character letter) {
        return this.withValue(letter);
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
