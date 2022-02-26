package generator.words.lowLevel;

import board.words.Word;
import player.letters.Rack;

public class WordBonusCalculator {

    public static int calculateBonus(Word.WordCandidate candidate, Rack startingRack) {
        return bingo(startingRack, candidate.rack());
    }

    private static int bingo(Rack startingRack, Rack finalRack) {
        int startingRackSize = startingRack.size();
        int finalRackSize = finalRack.size();
        // equal 0 only if startingRackSize was 7 and we've cleared the entire rack
        return 7 - startingRackSize + finalRackSize == 0 ?
                50 :
                0;
    }
}
