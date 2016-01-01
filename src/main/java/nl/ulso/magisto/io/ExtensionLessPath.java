package nl.ulso.magisto.io;

import java.nio.file.Path;

/**
 * Represents a path with the extension removed and available separately.
 */
public class ExtensionLessPath {

    private final Path pathWithoutExtension;
    private final String originalExtension;

    ExtensionLessPath(Path path) {
        final String filename = path.getName(path.getNameCount() - 1).toString();
        final int position = filename.lastIndexOf('.');
        if (position < 1) {
            pathWithoutExtension = path;
            originalExtension = "";
        } else {
            pathWithoutExtension = path.resolveSibling(filename.substring(0, position));
            originalExtension = filename.substring(position + 1);
        }
    }

    public Path getPathWithoutExtension() {
        return pathWithoutExtension;
    }

    public String getOriginalExtension() {
        return originalExtension;
    }
}
