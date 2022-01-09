package player.letters;

import lombok.experimental.UtilityClass;

import static player.letters.Rack.RackFactory.emptyRack;

@UtilityClass
public class RackUtil {

    public Rack randomRack(LetterBag bag) {
        Rack rack = emptyRack();
        return randomizeRack(bag, rack);
    }

    public Rack drawLetters(LetterBag bag, Rack rack) {
        return randomizeRack(bag, rack);
    }

    private Rack randomizeRack(LetterBag bag, Rack target) {
        int searchAmount = 7 - target.size();
        for (int i = searchAmount; i < 7; i++) {
            if (bag.size() > 0) {
                target.getLetters().add(bag.randomLetter());
            }
        }
        return target;
    }
}
