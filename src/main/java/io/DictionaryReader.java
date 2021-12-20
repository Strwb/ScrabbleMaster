package io;

import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.io.InputStream.nullInputStream;
import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DictionaryReader implements Reader {

    static String DICTIONARY_FILE_NAME = "dictionary_pl.txt";
    static String TEST_DICTIONARY_FILE_NAME = "test_dictionary_pl.txt";
    static String DICTIONARY_FILE_PATH = "dictionaries/" + DICTIONARY_FILE_NAME;
    static String TEST_DICTIONARY_FILE_PATH = "dictionaries/" + TEST_DICTIONARY_FILE_NAME;

    @Override
    public List<String> read() {
        Supplier<Optional<InputStream>> stream = readFileToStream(DICTIONARY_FILE_PATH);
        return processStream(stream);
    }

    @Override
    public List<String> readTest() {
        Supplier<Optional<InputStream>> stream = readFileToStream(TEST_DICTIONARY_FILE_PATH);
        return processStream(stream);
    }

    @SneakyThrows
    private static List<String> processStream(Supplier<Optional<InputStream>> stream) {
        List<String> words = new ArrayList<>();
        try (
                InputStream is = stream.get().orElse(nullInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line);
            }
        }
        return words;
    }
}
