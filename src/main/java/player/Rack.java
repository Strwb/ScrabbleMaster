package player;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.List;

import static util.lists.Lists.modifiableCopyOf;
import static util.lists.Lists.modifiableEmptyList;

@Value
@Builder
@With
public class Rack {

    List<Character> letters;

    public Rack withoutLetter(Character letter) {
        List<Character> lettersCopy = modifiableCopyOf(letters);
        lettersCopy.remove(letter);
        return this.withLetters(lettersCopy);
    }

    public boolean addLetter(Character letter) {
        if (letters.size() >= 7) {
            return false;
        }
        letters.add(letter);
        return true;
    }

    public static class RackFactory {

        public static Rack emptyRack() {
            return Rack.builder()
                    .letters(modifiableEmptyList())
                    .build();
        }
    }
}
