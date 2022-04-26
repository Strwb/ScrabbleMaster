package generator.words.lowLevel;

import board.words.Word.WordCandidate;

import java.util.List;

public interface WordGenerator {

   List<WordCandidate> generatedWords();

   void generate();

   // if anchor is center (beginning move) then that space condition is necessary, otherwise we don't need it
   default boolean startCheckPassed(int anchorRow, int anchorCol, int space) {
      boolean isAnchorCenter = (anchorRow == 7) && (anchorCol == 7);
      return isAnchorCenter ?
              space >= 0 :
              true;
   }
}
