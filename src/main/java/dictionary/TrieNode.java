package dictionary;

import lombok.Builder;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Collections.emptyMap;

@Value
@Builder
public class TrieNode {

    Character letter;

    Map<Character, TrieNode> neighbours;

    public void addNeighbour(String payload) {
        if (payload.length() > 0) {
            add(payload);
        }
    }

    public boolean hasNeighbour(Character character) {
        return neighbours.containsKey(character);
    }

    public boolean isTerminal() {
        return neighbours.isEmpty();
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

    private boolean letterAmongChildren(Character firstLetter) {
        return neighbours.keySet().stream().anyMatch(key -> key.equals(firstLetter));
    }

    public static TrieNode emptyDictionaryNode() {
        return TrieNode.builder()
                .letter(' ')
                .neighbours(Collections.emptyMap())
                .build();
    }
}
