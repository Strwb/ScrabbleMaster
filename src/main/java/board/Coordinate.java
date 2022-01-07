package board;

import lombok.Builder;

@Builder
public record Coordinate(int row, int col) {

    public static Coordinate fromAnchor(Board.Anchor anchor) {
        return new Coordinate(anchor.row(), anchor.col());
    }
};
