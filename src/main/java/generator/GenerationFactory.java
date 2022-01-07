package generator;

import board.Board;
import board.Board.Anchor;
import board.Word;
import dictionary.ScrabbleDictionary;
import lombok.experimental.FieldDefaults;
import player.Rack;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static generator.GeneratorUtil.horizontalGenerator;
import static generator.GeneratorUtil.verticalGenerator;
import static lombok.AccessLevel.PRIVATE;
import static util.lists.Lists.modifiableEmptyList;

@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GenerationFactory {

    ScrabbleDictionary dictionary;

    public GenerationFactory(ScrabbleDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Optional<Word> findNextMove(Board board, Rack rack, Set<Anchor> anchors) {
        Optional<Word> horizontal = generateMove(board, rack, anchors);
        horizontal.ifPresent(word -> System.out.println("HIGHEST SCORING: " + word.stringForm()));

        return horizontal;
    }

    // sam ruch -> 40ms, 52, 39, 49, 45 (4 watki)
    // sam ruch ->  64, 42, 42, 41 (1 watek)
    private Optional<Word> generateMove(Board board, Rack rack, Set<Anchor> anchors) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Callable<List<Word>>> executorTasks = modifiableEmptyList();
        anchors.forEach(anchor -> {
            switch (anchor.type()) {
                case HORIZONTAL -> executorTasks.add(horizontalGenerator(anchor, board, rack, dictionary));
                case VERTICAL -> executorTasks.add(verticalGenerator(anchor, board, rack, dictionary));
            }
        });
        List<Future<List<Word>>> results;
        try {
            results = executorService.invokeAll(executorTasks);

        } catch (InterruptedException e) {
            throw new RuntimeException("Error during horizontal generation");
        }

        return GeneratorUtil.getMaxWord(results);
    }
}
