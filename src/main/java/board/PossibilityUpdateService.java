package board;

import board.Board.Anchor;
import generator.GenerationFactory;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Set;

import static board.BoardUtil.getAnchors;
import static java.util.Collections.singletonList;

@Value
@Builder
public class PossibilityUpdateService {

    GenerationFactory generator;

    public void updatePossibilities(Board board, Word word) {
        Set<Anchor> neighbours = getAnchors(singletonList(word));
        //TODO:
        // 1. Fire up generation task for every anchor
        // 2. Update possibilities on board
    }

    public static record Possibility(int row, int col, PlacementType type, List<Character> possibilities) {}
}
