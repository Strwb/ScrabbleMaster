package dictionary;

import java.util.List;

import static java.util.Collections.singletonList;

public class Score {

    private static final List<Character> ones = List.of(
            'a',
            'i',
            'e',
            'o',
            'n',
            'z',
            'r',
            's',
            'w'
    );

    private static final List<Character> twos = List.of(
            'y',
            'c',
            'd',
            'k',
            'l',
            'm',
            'p',
            't'
    );

    private static final List<Character> threes = List.of(
            'b',
            'g',
            'h',
            'j',
            'ł',
            'u'
    );

    private static final List<Character> fives = List.of(
            'ą',
            'ę',
            'f',
            'ó',
            'ś',
            'ż'
    );

    private static final List<Character> six = singletonList('ć');

    private static final List<Character> seven = singletonList('ń');

    private static final List<Character> nine = singletonList('ż');

    public static int checkLetterValue(Character letter) {
        if (ones.contains(letter)) {
            return 1;
        }
        if (twos.contains(letter)) {
            return 2;
        }
        if (threes.contains(letter)) {
            return 3;
        }
        if (fives.contains(letter)) {
            return 5;
        }
        if (six.contains(letter)) {
            return 6;
        }
        if (seven.contains(letter)) {
            return 7;
        }
        if (nine.contains(letter)) {
            return 9;
        }
        return 0;
    }
}
