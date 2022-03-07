package generator.words.lowLevel;

import board.board.Board;
import board.board.fields.BoardTraverser;
import board.board.fields.Field;
import board.words.Word.WordCandidate;
import board.words.WordUtil;
import dictionary.ScrabbleDictionary;
import dictionary.TrieNode;

import java.util.ArrayList;
import java.util.List;

public class SideGeneratorUtil {

    public static boolean isWordValid(WordCandidate candidate, Board board, ScrabbleDictionary dictionary) {
        return switch (candidate.word().getType()) {
            case VERTICAL -> isVerticalWordValid(candidate, board, dictionary);
            case HORIZONTAL -> isHorizontalWordValid(candidate, board, dictionary);
        };
    }

    private static boolean isVerticalWordValid(WordCandidate candidate, Board board, ScrabbleDictionary dictionary) {
        List<Coordinate> coordinates = Coordinate.extractVerticalCoordinates(candidate);
        boolean horizontalCorrectness = coordinates.stream().allMatch(coordinate -> formsHorizontalWord(candidate, board, coordinate, dictionary));
        boolean verticalCorrectness = formsVerticalWord(candidate, board, coordinates.get(0), dictionary);
        return horizontalCorrectness && verticalCorrectness;
    }

    private static boolean formsHorizontalWord(WordCandidate candidate, Board board, Coordinate coordinate, ScrabbleDictionary dictionary) {
        int leftMostStart = BoardTraverser.getOccupiedLeftDistance(board, coordinate.row(), coordinate.col());
        Board tempBoard = board.addWords(candidate.word());
        List<Field> fields = BoardTraverser.goAllTheWayRight(new board.board.Coordinate(coordinate.row(), coordinate.col() - leftMostStart), tempBoard);
//        fields = reverse(fields);
        TrieNode boardEnd = dictionary.findWord(WordUtil.stringForm(fields));
//        System.out.println("HORIZONTAL: " + fields);
        return (boardEnd.isCorrect() && boardEnd.isEndOfWord()) || (fields.size() <= 1);
    }

    private static boolean isHorizontalWordValid(WordCandidate candidate, Board board, ScrabbleDictionary dictionary) {
        List<Coordinate> coordinates = Coordinate.extractHorizontalCoordinates(candidate);
        boolean verticalCorrectness = coordinates.stream().allMatch(coordinate -> formsVerticalWord(candidate, board, coordinate, dictionary));
        boolean horizontalCorrectness = formsHorizontalWord(candidate, board, coordinates.get(0), dictionary);
        return verticalCorrectness && horizontalCorrectness;
    }

    private static boolean formsVerticalWord(WordCandidate candidate, Board board, Coordinate coordinate, ScrabbleDictionary dictionary) {
        int aboveMostStart = BoardTraverser.getOccupiedAboveDistance(board, coordinate.row(), coordinate.col());
        Board tempBoard = board.addWords(candidate.word());
        List<Field> fields = BoardTraverser.goAllTheWayDown(new board.board.Coordinate(coordinate.row() - aboveMostStart, coordinate.col()), tempBoard);
//        fields = reverse(fields);
        TrieNode boardEnd = dictionary.findWord(WordUtil.stringForm(fields));
//        System.out.println("VERTICAL: " + fields);
        return (boardEnd.isCorrect() && boardEnd.isEndOfWord()) || (fields.size() <= 1);
    }

    private record Coordinate(int row, int col, Character value) {

        static List<Coordinate> extractVerticalCoordinates(WordCandidate candidate) {
            int startRow = candidate.word().getStart();
            int startCol = candidate.word().getVectorNo();
            List<Coordinate> coordinates = new ArrayList<>();
            int index = 0;

            for (int row = startRow; row < startRow + candidate.word().getLength(); row++) {
                coordinates.add(new Coordinate(row, startCol, candidate.word().getLetters().get(index).getValue()));
                index++;
            }
            return coordinates.stream().toList();
        }

        static List<Coordinate> extractHorizontalCoordinates(WordCandidate candidate) {
            int startRow = candidate.word().getVectorNo();
            int startCol = candidate.word().getStart();
            List<Coordinate> coordinates = new ArrayList<>();
            int index = 0;

            for (int col = startCol; col < startCol + candidate.word().getLength(); col++) {
                coordinates.add(new Coordinate(startRow, col, candidate.word().getLetters().get(index).getValue()));
                index++;
            }
            return coordinates.stream().toList();
        }
    }
}
