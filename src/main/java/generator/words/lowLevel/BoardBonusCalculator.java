package generator.words.lowLevel;

import board.board.Board;
import board.board.fields.BoardTraverser;
import board.board.fields.Field;
import dictionary.ScrabbleDictionary;
import dictionary.TrieNode;
import lombok.experimental.UtilityClass;

import java.util.Optional;


@UtilityClass
public class BoardBonusCalculator {

    //TODO
    // 1. Podpiac to liczenie w odpowiednie miejsca w generatorach
    // 1.5. Candydaci powinni miec pole neighboursScore, ktory powinien sie aktualizowac z kazda dolozona literka
    // 2. Napisac jakis prosty test
    // 3. Sprawdzic czy obsluga bingo faktycznie dziala
    // 4. Zrobic refactor -> Zamienic kopie w serializacje (fun part)
    // 5. Podwieczorek -> Ruch rozpoczynajacy


    // for horizontal generation
    public int verticalNeighbourBonus(ScrabbleDictionary dictionary, Board board, Character letter, int row, int col) {
        int verticalStart = verticalStart(board, row, col);
        return countDown(dictionary, board, verticalStart, col, row, letter);
    }

    // for vertical generation
    public int horizontalNeighbourBonus(ScrabbleDictionary dictionary, Board board, Character letter, int row, int col) {
        int horizontalStart = horizontalStart(board, row, col);
        return countRight(dictionary, board, row, horizontalStart, col, letter);
    }

    private int verticalStart(Board board, int row, int col) {
        return hasNeighbourAbove(board, row, col) ?
                BoardTraverser.getOccupiedAboveDistance(board, row, col) :
                row;
    }

    private int horizontalStart(Board board, int row, int col) {
        return hasLeftNeighbour(board, row, col) ?
                BoardTraverser.getOccupiedLeftDistance(board, row, col) :
                col;
    }

    private boolean hasNeighbourAbove(Board board, int row, int col) {
        return board.checkField(row - 1, col).isPresent();
    }

    private boolean hasLeftNeighbour(Board board, int row, int col) {
        return board.checkField(row, col - 1).isPresent();
    }

    private int countDown(ScrabbleDictionary dictionary, Board board, int row, int col, int targetRow, Character letter) {
        int sum = 0;
        TrieNode node = dictionary.start();
        Optional<Field> field = board.checkField(row, col);
        while (field.isPresent() && field.get().isOccupied() && node.hasNeighbour(field.get().getValue())) {
            sum += field.get().letterValue();
            node = node.getNeighbours().get(field.get().getValue());
            if (row != targetRow) {
                row++;
            } else {
                row += 2;
            }
            field = board.checkField(row, col);
        }
        return node.isEndOfWord() ?
                sum :
                0;
    }

    private int countRight(ScrabbleDictionary dictionary, Board board, int row, int col, int targetCol, Character letter) {
        int sum = 0;
        TrieNode node = dictionary.start();
        Optional<Field> field = board.checkField(row, col);
        while (field.isPresent() && field.get().isOccupied() && node.hasNeighbour(field.get().getValue())) {
            sum += field.get().letterValue();
            node = node.getNeighbours().get(field.get().getValue());
            if (col != targetCol) {
                col++;
            } else {
                row += 2;
            }
            field = board.checkField(row, col);
        }
        return node.isEndOfWord() ?
                sum :
                0;
    }
}
