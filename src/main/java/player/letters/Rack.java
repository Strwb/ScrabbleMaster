package player.letters;

import board.words.Word;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.List;

import static util.lists.Lists.modifiableCopyOf;
import static util.lists.Lists.modifiableEmptyList;

@Value
@Builder
@With
public class Rack implements Serializable {

    List<Character> letters;

    public Rack withoutLetter(Character letter) {
        List<Character> lettersCopy = modifiableCopyOf(letters);
        lettersCopy.remove(letter);
        return this.withLetters(lettersCopy);
    }

    public Rack withoutLetters(Word word) {
        Rack clone = this.clone();
        word.getLetters().forEach(letter -> clone.getLetters().remove(letter.getValue()));
        return clone;
    }

    public Rack addLetter(Character letter) {
        if (letters.size() >= 7) {
            return this;
        }
        List<Character> lettersCopy = modifiableCopyOf(letters);
        lettersCopy.add(letter);
        return this.withLetters(lettersCopy);
    }

    public int size() {
        return letters.size();
    }

    public static class RackFactory {

        public static Rack emptyRack() {
            return Rack.builder()
                    .letters(modifiableEmptyList())
                    .build();
        }
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        letters.forEach(letter -> sb.append(letter).append(" "));
        return sb.toString();
    }

    public Rack clone() {
        return SerializationUtils.clone(this);
    }
}
