package shared;

import board.Board;
import board.Field;
import board.Word;
import player.Rack;

import java.util.List;

import static board.PlacementType.HORIZONTAL;
import static board.PlacementType.VERTICAL;

public class TestEntities {

    public static List<Word> createTwoTestWords(Board board) {
        return List.of(createDupa(board), createUrna(board));
    }

    private static Word createDupa(Board board) {
        Field d = board.checkField(7, 7).get().withLetter('d');
        Field u = board.checkField(7, 8).get().withLetter('u');
        Field p = board.checkField(7, 9).get().withLetter('p');
        Field a = board.checkField(7, 10).get().withLetter('a');
        return Word.builder()
                .letters(List.of(d, u, p, a))
                .type(HORIZONTAL)
                .vectorNo(7)
                .start(7)
                .build();
    }

    private static Word createUrna(Board board) {
        Field r = board.checkField(8, 8).get().withLetter('r');
        Field n = board.checkField(9, 8).get().withLetter('n');
        Field a = board.checkField(10, 8).get().withLetter('a');
        return Word.builder()
                .letters(List.of(r, n, a))
                .type(VERTICAL)
                .vectorNo(8)
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
        return rack.withLetters(List.of('a', 'g', 'e', 'z', 'p', 'e', 'i'));
    }
}
