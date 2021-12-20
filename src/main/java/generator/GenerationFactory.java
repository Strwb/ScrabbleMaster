package generator;

import board.Board;
import board.Board.Anchor;
import board.Word;
import dictionary.ScrabbleDictionary;
import lombok.experimental.FieldDefaults;
import player.Rack;
import util.lists.Lists;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static board.PlacementType.HORIZONTAL;
import static dictionary.ScrabbleDictionary.scrabbleDictionary;
import static generator.GeneratorUtil.horizontalAnchors;
import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GenerationFactory {

    ScrabbleDictionary dictionary;

    public GenerationFactory() {
        dictionary = scrabbleDictionary();
        dictionary.loadDictionary();
    }

    public Optional<Word> findNextMove(Board board, Rack rack, Set<Anchor> anchors) {
        //TODO:
        // - Filter out vertical anchors
        // - Get the highest scoring word
        Optional<Word> horizontal = horizontalMove(board, rack, horizontalAnchors(anchors));
//        Word vertical = verticalMove(board, rack, verticalAnchors(anchors));
        return horizontal;

    }

    private Optional<Word> horizontalMove(Board board, Rack rack, Set<Anchor> anchors) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
//        List<Future<List<Word>>> futures = modifiableEmptyList();
        List<Callable<List<Word>>> executorTasks = Lists.modifiableEmptyList();
        anchors.forEach(anchor -> {
            executorTasks.add(
                    HorizontalGenerator.builder()
                            .dictionary(dictionary)
                            .anchorRow(anchor.row())
                            .anchorCol(anchor.col())
                            .type(HORIZONTAL)
                            .board(board)
                            .limit(7)
                            .startingRack(rack)
                            .words(Lists.modifiableEmptyList())
                            .build()
            );
        });
        List<Future<List<Word>>> results = Lists.modifiableEmptyList();
        try {
             results = executorService.invokeAll(executorTasks);

        } catch (InterruptedException e) {
            throw new RuntimeException("Wyjebilo sie przy generacji horyzontalnej");
        }

        return GeneratorUtil.getMaxWord(results);

    }


//    private Word verticalMove(Board board, Rack rack, Set<Anchor> anchors) {
//
//    }
}
