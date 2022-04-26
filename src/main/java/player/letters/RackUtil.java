package player.letters;

import lombok.experimental.UtilityClass;

import static player.letters.Rack.RackFactory.emptyRack;

@UtilityClass
public class RackUtil {

    /**
     * This method automatically deletes used letters from the passed bag
     * @return rack of letters randomly drawn from the bag
     */
    public Rack randomRack(LetterBag bag) {
        Rack rack = emptyRack();
        return randomizeRack(bag, rack);
    }

    public Rack drawLetters(LetterBag bag, Rack rack) {
        return randomizeRack(bag, rack);
    }

    private Rack randomizeRack(LetterBag bag, Rack target) {
        int searchAmount = 7 - target.size();
        for (int i = searchAmount; i > 0; i--) {
            if (bag.size() > 0) {
                target = target.addLetter(bag.randomLetter());
            }
        }
        return target;
    }
}
