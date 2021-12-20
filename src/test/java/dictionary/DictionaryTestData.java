package dictionary;

import java.util.List;

import static dictionary.ScrabbleDictionary.scrabbleDictionary;

public class DictionaryTestData {

    static Trie givenEmptyTrie() {
        return Trie.buildTrie();
    }

    static ScrabbleDictionary givenEmptyScrabbleDictionary() {
        return scrabbleDictionary();
    }
}
