package shared;

import board.board.Board;
import board.board.fields.Field;
import board.words.Word;
import player.letters.Rack;

import java.util.List;

import static board.board.fields.PlacementType.HORIZONTAL;
import static board.board.fields.PlacementType.VERTICAL;

public class TestEntities {

    public static List<Word> createTwoTestWordsHorizontalScenario(Board board) {
        return List.of(createDomek(board), createKotek(board));
    }

    public static List<Word> createTwoTestWordsVerticalScenario(Board board) {
        return List.of(createVerticalDomek(board), createVerticalKotek(board));
    }

    public static List<Word> createWordValidationScenario(Board board) {
        return List.of(createDomek(board), createKotek(board), createEra(board));
    }

    private static Word createDomek(Board board) {
        Field d = board.checkField(7, 7).get().withLetter('d');
        Field o = board.checkField(7, 8).get().withLetter('o');
        Field m = board.checkField(7, 9).get().withLetter('m');
        Field k = board.checkField(7, 10).get().withLetter('k');
        Field i = board.checkField(7, 11).get().withLetter('i');

        return Word.builder()
                .letters(List.of(d, o, m, k, i))
                .type(HORIZONTAL)
                .vectorNo(7)
                .start(7)
                .build();
    }

    private static Word createVerticalDomek(Board board) {
        Field d = board.checkField(7, 7).get().withLetter('d');
        Field o = board.checkField(7, 8).get().withLetter('o');
        Field m = board.checkField(7, 9).get().withLetter('m');
        Field k = board.checkField(7, 10).get().withLetter('k');
        Field i = board.checkField(7, 11).get().withLetter('i');

        return Word.builder()
                .letters(List.of(d, o, m, k, i))
                .type(VERTICAL)
                .vectorNo(7)
                .start(7)
                .build();

    }

    private static Word createKotek(Board board) {
        Field k = board.checkField(6, 8).get().withLetter('k');
        Field o = board.checkField(7, 8).get().withLetter('o');
        Field t = board.checkField(8, 8).get().withLetter('t');
        Field e = board.checkField(9, 8).get().withLetter('e');
        Field k1 = board.checkField(11, 8).get().withLetter('k');
        return Word.builder()
                .letters(List.of(k, o, t, e, k1))
                .type(VERTICAL)
                .vectorNo(8)
                .start(6)
                .build();
    }

    private static Word createVerticalKotek(Board board) {
        Field k = board.checkField(6, 8).get().withLetter('k');
        Field o = board.checkField(7, 8).get().withLetter('o');
        Field t = board.checkField(8, 8).get().withLetter('t');
        Field e = board.checkField(9, 8).get().withLetter('e');
        Field k1 = board.checkField(11, 8).get().withLetter('k');
        return Word.builder()
                .letters(List.of(k, o, t, e, k1))
                .type(HORIZONTAL)
                .vectorNo(8)
                .start(6)
                .build();
    }

    private static Word createEra(Board board) {
        Field e = board.checkField(9, 8).get().withLetter('e');
        Field r = board.checkField(9, 9).get().withLetter('r');
        Field a = board.checkField(9, 10).get().withLetter('a');

        return Word.builder()
                .letters(List.of(e, r, a))
                .type(HORIZONTAL)
                .vectorNo(9)
                .start(8)
                .build();
    }

    public static Word highScoringWord(Board board) {
        Field p = board.checkField(7, 7).get().withLetter('p');
        Field i = board.checkField(7, 8).get().withLetter('i');
        Field e = board.checkField(7, 9).get().withLetter('e');
        Field n = board.checkField(7, 10).get().withLetter('n');
        Field i1 = board.checkField(7, 11).get().withLetter('i');
        Field a = board.checkField(7, 12).get().withLetter('ą');
        Field d = board.checkField(7, 13).get().withLetter('d');
        Field z = board.checkField(7, 14).get().withLetter('z');
        return Word.builder()
                .letters(List.of(p, i, e, n, i1, a, d, z))
                .type(HORIZONTAL)
                .vectorNo(7)
                .start(7)
                .build();
    }

    public static Rack createTestRack() {
        Rack rack = Rack.RackFactory.emptyRack();
        return rack.withLetters(List.of('d', 'f', 'e', 'a', 'ą', 'r', 'i'));
//        return rack.withLetters(List.of('a', 'g', 'e', 'z', 'p', 'e', 'i'));
//        return rack.withLetters(List.of('a', 'd', 'k'));
//        return rack.withLetters(List.of('e', 'k'));
//        return rack.withLetters(List.of('d', 'e', 'a'));
    }
}
