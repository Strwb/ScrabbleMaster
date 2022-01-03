package dictionary;

import lombok.Builder;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyMap;

@Value
@Builder
public class TrieNode {

    Character letter;

    Map<Character, TrieNode> neighbours;

    @Builder.Default
    boolean endOfWord = false;

    public void addNeighbour(String payload) {
        if (payload.length() > 1) {
            add(payload);
        } else if (payload.length() == 1) {
            addTerminal(payload);
        }
    }

    public boolean hasNeighbour(Character character) {
        return neighbours.containsKey(character);
    }

    public boolean isError() {
        return letter == '@';
    }

    public boolean isTerminal() {
        return neighbours.isEmpty();
    }

    public boolean isEndOfWord() {
        return endOfWord;
    }

    public boolean isCorrect() {
        return letter != ' ' &&
                letter != '/' &&
                letter != '@';
    }

    public Optional<TrieNode> getNeighbour(Character key) {
        return Optional.ofNullable(neighbours.get(key));
    }

    private void add(String payload) {
        Character firstLetter = payload.charAt(0);
        String cutPayload = payload.substring(1);
        if (!letterAmongChildren(firstLetter)) {
            neighbours.put(
                    firstLetter,
                    TrieNode.builder()
                            .letter(firstLetter)
                            .neighbours(new HashMap<>())
                            .build()
            );
        }
        neighbours.get(firstLetter).addNeighbour(cutPayload);
    }

    private void addTerminal(String payload) {
        Character firstLetter = payload.charAt(0);
        if (!letterAmongChildren(firstLetter)) {
            neighbours.put(
                    firstLetter,
                    TrieNode.builder()
                            .letter(firstLetter)
                            .neighbours(new HashMap<>())
                            .endOfWord(true)
                            .build()
            );
        }
    }

    private boolean letterAmongChildren(Character firstLetter) {
        return neighbours.keySet().stream().anyMatch(key -> key.equals(firstLetter));
    }

    public static TrieNode emptyDictionaryNode() {
        return TrieNode.builder()
                .letter(' ')
                .neighbours(emptyMap())
                .build();
    }

    public static TrieNode errorNode() {
        return TrieNode.builder()
                .letter('@')
                .neighbours(emptyMap())
                .build();
    }
}
