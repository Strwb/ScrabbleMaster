package generator;

import board.Board;
import board.Word;

import java.util.Set;
import java.util.concurrent.Callable;

import static board.Board.Anchor;

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
