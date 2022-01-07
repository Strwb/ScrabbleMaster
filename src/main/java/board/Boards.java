package board;

import lombok.experimental.UtilityClass;
import util.Arrays.Arrays;
import util.lists.Lists;

import java.util.List;

@UtilityClass
public class Boards {

    Board addWord(List<Word> words, Board board) {
        Board copy = Board.builder()
                .board(Arrays.deepCopyOf(board.getBoard()))
                .words(Lists.modifiableCopyOf(board.getWords()))
                .build();

        words.forEach(copy::attemptAddition);
        return copy;
    }
}
