package nl.ulso.magisto.io;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Set;

/**
 * Utility methods on {@link Path}s.
 */
public final class Paths {

    private Paths() {
    }

    public static Path requireAbsolutePath(Path path) {
        if (!path.isAbsolute()) {
            throw new IllegalStateException("Not an absolute path: " + path);
        }
        return path;
    }

    public static Path requireRelativePath(Path path) {
        if (path.isAbsolute()) {
            throw new IllegalStateException("Not a relative path: " + path);
        }
        return path;
    }

    public static Path createPath(String first, String... more) {
        return FileSystems.getDefault().getPath(first, more);
    }

    public static ExtensionLessPath splitOnExtension(Path path) {
        return new ExtensionLessPath(path);
    }

    public static Comparator<Path> prioritizeOnExtension(String... extensions) {
        return new PrioritizedByExtensionPathComparator(extensions);
    }

    public static Comparator<Path> prioritizeOnExtension(Set<String> extensions) {
        return new PrioritizedByExtensionPathComparator(extensions);
    }

}
