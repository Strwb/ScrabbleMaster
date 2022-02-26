package generator.words.lowLevel;

import board.words.Word.WordCandidate;

import java.util.List;

public interface WordGenerator {

   List<WordCandidate> generatedWords();

   void generate();
}
