package generator.possibilities;

import board.board.Board;
import board.board.Board.Anchor;
import board.board.Coordinate;
import dictionary.ScrabbleDictionary;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class PossibilitiesFactory {

    ScrabbleDictionary dictionary;

    public PossibilitiesFactory(ScrabbleDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Set<StampedAnchor> generatePossibilities(Set<Anchor> anchors, Board board) {
        return anchors.stream()
                .map(Coordinate::fromAnchor)
                .map(coordinate -> new PossibilitiesGenerator(dictionary, coordinate, board))
                .map(PossibilitiesFactory::extractPossibilities)
                .collect(toSet());

    }

    private static StampedAnchor extractPossibilities(PossibilitiesGenerator generator) {
        return new StampedAnchor(
                generator.getCoordinate().row(),
                generator.getCoordinate().col(),
                generator.findPossibilities());
    }

    public record StampedAnchor(int row, int col, Set<Character> possibilities){};
}
