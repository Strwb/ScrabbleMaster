package generator.possibilities;

import board.board.Board;
import board.board.Coordinate;
import dictionary.ScrabbleDictionary;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

import static com.google.common.collect.Sets.intersection;

@Value
@Builder
public class PossibilitiesGenerator {

    ScrabbleDictionary dictionary;
    Coordinate coordinate;
    Board board;

    public Set<Character> findPossibilities() {
        Set<Character> up = up().findPossibilities();
        Set<Character> left = left().findPossibilities();
        Set<Character> down = down().findPossibilities();
        Set<Character> right = right().findPossibilities();

        Set<Character> vertical = intersection(up, down);
        Set<Character> horizontal = intersection(left, right);

        return intersection(vertical, horizontal);
    }

    private UpPossibilities up() {
        return UpPossibilities.builder()
                .board(board)
                .coordinate(coordinate)
                .dictionary(dictionary)
                .build();
    }

    private LeftPossibilities left() {
        return LeftPossibilities.builder()
                .board(board)
                .coordinate(coordinate)
                .dictionary(dictionary)
                .build();
    }

    private DownPossibilities down() {
        return DownPossibilities.builder()
                .board(board)
                .coordinate(coordinate)
                .dictionary(dictionary)
                .build();
    }

    private RightPossibilities right() {
        return RightPossibilities.builder()
                .board(board)
                .coordinate(coordinate)
                .dictionary(dictionary)
                .build();
    }
}
