package dictionary;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dictionary.DictionaryTestData.givenEmptyScrabbleDictionary;
import static org.assertj.core.api.Assertions.assertThat;

public class ScrabbleDictionaryUnitTest {

    @Test
    public void shouldAddSingleWord() {
        var scrabbleDictionary = givenEmptyScrabbleDictionary();
        scrabbleDictionary.add("uwaga");
        assertThat(scrabbleDictionary.getDictionary().getRoot().getNeighbours().size()).isEqualTo(1);
    }

    @Test
    public void shouldAddMultipleWords() {
        var scrabbleDictionary = givenEmptyScrabbleDictionary();
        List<String> words = List.of("Artur", "anna", "bartek", "sylwia", "Weronika", "jan");
        scrabbleDictionary.add(words);
        assertThat(scrabbleDictionary.getDictionary().getRoot().getNeighbours().size()).isEqualTo(5);
    }

    @Test
    public void shouldLoadTestDictionary() {
        var scrabbleDictionary = givenEmptyScrabbleDictionary();
        scrabbleDictionary.loadTestDictionary();
        assertThat(scrabbleDictionary.getDictionary().getRoot().getNeighbours().size()).isEqualTo(10);
    }

    @Test
    @Disabled
    public void shouldLoadMainDictionary() { // use with caution
        var scrabbleDictionary = givenEmptyScrabbleDictionary();
        scrabbleDictionary.loadDictionary();
        assertThat(scrabbleDictionary.getDictionary().getRoot().getNeighbours().size()).isEqualTo(32);
    }
}
