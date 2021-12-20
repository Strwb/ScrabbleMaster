package dictionary;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TrieUnitTest {

    @Test
    public void shouldAssertTrue() {
        Trie trie = DictionaryTestData.givenEmptyTrie();
        List<String> words = List.of("być", "robić", "auto", "drzewo");
        words.forEach(trie::addWord);
        assertThat(trie.getRoot().getNeighbours().size()).isEqualTo(4);
    }

    @Test
    public void shouldFindWord() {
        Trie trie = DictionaryTestData.givenEmptyTrie();
        List<String> words = List.of("być", "robić", "auto", "drzewo");
        words.forEach(trie::addWord);
        assertThat(trie.findWord("drzewo").getLetter()).isEqualTo('o');
    }
}
