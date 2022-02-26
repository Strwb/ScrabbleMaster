package dictionary;

import lombok.Builder;
import lombok.Value;

import java.util.*;

import static dictionary.TrieNode.errorNode;
import static java.util.Collections.emptyMap;

@Builder
@Value
public class Trie { //TODO test this shit

    TrieNode root;

    public void addWord(String word) {
        root.addNeighbour(word.toLowerCase(Locale.ROOT));
    }

    public void addWords(List<String> words) {
        words.forEach(this::addWord);
    }

    public void printTree() {
        breathFirstPrint();
    }

    public static Trie buildTrie() {
        return Trie.builder()
                .root(TrieNode.builder()
                        .letter('/')
                        .neighbours(new HashMap<>())
                        .build())
                .build();
    }

    public TrieNode findWord(String word) {
        return findWord(word, 0, root);
    }

    private TrieNode findWord(String word, int letterIndex, TrieNode node) {
        if ((letterIndex < word.length()) && node.hasNeighbour(word.charAt(letterIndex))) {
            return findWord(word, letterIndex + 1, node.getNeighbour(word.charAt(letterIndex)).get());
        } else if (letterIndex >= word.length()) {
            return node;
        } else {
            return errorNode();
        }
    }

    private void breathFirstPrint() {
        TrieNode levelEnd = TrieNode.builder()
                .letter('-')
                .neighbours(emptyMap())
                .build();
        Queue<TrieNode> nodeQueue = new ArrayDeque<>();
        nodeQueue.add(root);
        for (TrieNode node : nodeQueue) {
            System.out.println(root.getLetter());
            nodeQueue.add(levelEnd);
            nodeQueue.addAll(node.getNeighbours().values());
        }
    }
}
