package nl.ulso.magisto.io;

import java.nio.file.Path;
import java.util.*;

import static nl.ulso.magisto.io.Paths.splitOnExtension;

/**
 * Comparator for paths. Implements a normal path comparison, unless the path differ only on extension. In that case
 * the paths are ordered on extension priority first.
 * <p>
 * Extensions are always translated to lower case before comparison.
 * </p>
 *
 * @see nl.ulso.magisto.io.Paths#prioritizeOnExtension(String...)
 * @see nl.ulso.magisto.io.Paths#prioritizeOnExtension(java.util.Set)
 */
class PrioritizedByExtensionPathComparator implements Comparator<Path> {
    private final Set<String> extensions;

    PrioritizedByExtensionPathComparator(String[] extensions) {
        this(Arrays.asList(extensions));
    }

    PrioritizedByExtensionPathComparator(Collection<String> extensions) {
        final HashSet<String> set = new HashSet<>(extensions.size());
        for (String extension : extensions) {
            set.add(extension.toLowerCase());
        }
        this.extensions = Collections.unmodifiableSet(set);
    }

    @Override
    public int compare(Path first, Path second) {
        final ExtensionLessPath firstPath = splitOnExtension(first);
        final ExtensionLessPath secondPath = splitOnExtension(second);

        final int pathComparison = firstPath.getPathWithoutExtension().compareTo(secondPath.getPathWithoutExtension());
        if (pathComparison != 0) {
            return pathComparison;
        }

        final String firstExtension = firstPath.getOriginalExtension();
        if (extensions.contains(firstExtension.toLowerCase())) {
            return -1;
        }

        final String secondExtension = secondPath.getOriginalExtension();
        if (extensions.contains(secondExtension.toLowerCase())) {
            return 1;
        }

        return firstExtension.compareTo(secondExtension);
    }
}
