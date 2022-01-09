package generator.words;

import board.board.Board;
import board.words.Word;

import java.util.Set;
import java.util.concurrent.Callable;

import static board.board.Board.Anchor;

public class HorizontalPossibilitiesGenerator implements Callable<Set<Character>> {

    Board board;
    Anchor anchor;
    Word word;

    // find in which direction possibility generation should go

    @Override
    public Set<Character> call() throws Exception {
        return null;
    }
}
