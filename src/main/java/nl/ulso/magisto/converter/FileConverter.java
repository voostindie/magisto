package nl.ulso.magisto.converter;

import nl.ulso.magisto.io.FileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

/**
 * Converts files from one format to another.
 * <p>
 * <strong>Important!</strong>: The file name of the converted file must overlap with the original name as much as
 * possible. Only their extensions may differ. Otherwise the lexicographical ordering will not match, and the path
 * comparison algorithm in the Magisto class will go out of whack!
 * </p>
 */
public interface FileConverter {

    Set<String> getSourceExtensions();

    String getTargetExtension();

    boolean supports(Path path);

    Path getConvertedFileName(Path path);

    void convert(FileSystem fileSystem, Path sourceRoot, Path targetRoot, Path path) throws IOException;

    boolean isCustomTemplateChanged(FileSystem fileSystem, Path sourceRoot, Path targetRoot) throws IOException;
}
