package nl.ulso.magisto.converter;

import nl.ulso.magisto.io.FileSystem;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Factory for converters.
 */
public interface FileConverterFactory {

    FileConverter create(FileSystem fileSystem, Path sourceRoot) throws IOException;
}
