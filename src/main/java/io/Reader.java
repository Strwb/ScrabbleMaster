package io;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface Reader {

    List<String> read();

    List<String> readTest();

    default Supplier<Optional<InputStream>> readFileToStream(String filePath) {
        return () ->
                Optional.ofNullable(this.getClass().getClassLoader().getResourceAsStream(filePath));
    }
}
