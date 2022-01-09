package board.words;

import board.board.fields.Field;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static dictionary.Score.checkLetterValue;

public class WordUtil {

    public static Set<Character> wholeAlphabet() {
        return Set.of(
                'a',
                'ą',
                'b',
                'c',
                'ć',
                'd',
                'e',
                'ę',
                'f',
                'g',
                'h',
                'i',
                'j',
                'k',
                'l',
                'ł',
                'm',
                'n',
                'ń',
                'o',
                'ó',
                'p',
                'q',
                'r',
                's',
                'ś',
                't',
                'u',
                'v',
                'w',
                'x',
                'y',
                'z',
                'ź',
                'ż'
        );
    }

    public static List<Character> wholeAlphabetList() {
        return List.copyOf(wholeAlphabet());
    }

    public static Set<Character> noPossibilites() {
        return Collections.emptySet();
    }

    public static int calculateWordScore(List<Field> letters) {
        int multiplier = 1;
        int score = 0;
        for (Field letter : letters) {
            Character letterCharacter = letter.getValue();
            int letterScore = checkLetterValue(letterCharacter);
            switch (letter.getBonus()) {
                case DOUBLE_WORD -> multiplier *= 2;
                case TRIPLE_WORD -> multiplier *= 3;
                case DOUBLE_LETTER -> letterScore *= 2;
                case TRIPLE_LETTER -> letterScore *= 3;
            }
            score += letterScore;
        }
        return score * multiplier;
    }
}