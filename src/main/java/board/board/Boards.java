package board.board;

import board.words.Word;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.SerializationUtils;

import java.util.List;

@UtilityClass
public class Boards {

    Board addWord(List<Word> words, Board board) {
//        Board copy = Board.builder()
//                .board(Arrays.deepCopyOf(board.getBoard()))
//                .words(Lists.modifiableCopyOf(board.getWords()))
//                .build();

        Board copy = SerializationUtils.clone(board);

        words.forEach(copy::attemptAddition);
        return copy;
    }
}
