package dictionary;

import board.Word;
import io.DictionaryReader;
import io.Reader;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static dictionary.Trie.buildTrie;

@Value
@Builder
@Slf4j
public class ScrabbleDictionary {

    Trie dictionary;
    Reader reader;

    public void add(String word) {
        dictionary.addWord(word);
        log.info("Successfully added one word!");
    }

    public void add(List<String> words) {
        dictionary.addWords(words);
        log.info("Successfully added " + words.size() + " words!");
    }

    public void loadDictionary() {
        dictionary.addWords(reader.read());
        log.info("Successfully loaded the main dictionary!");
    }

    public void loadTestDictionary() {
        dictionary.addWords(reader.readTest());
        log.info("Successfully loaded test dictionary!");
    }

    public TrieNode findWord(Word word) {
        return dictionary.findWord(word.stringForm());
    }

    public static ScrabbleDictionary scrabbleDictionary() {
        return ScrabbleDictionary.builder()
                .dictionary(buildTrie())
                .reader(new DictionaryReader())
                .build();
    }
}