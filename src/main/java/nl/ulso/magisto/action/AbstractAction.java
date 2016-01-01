package nl.ulso.magisto.action;

import java.nio.file.Path;

import static nl.ulso.magisto.io.Paths.requireRelativePath;

/**
 * Abstract base class for {@link Action}s.
 */
abstract class AbstractAction implements Action {
    private final Path path;
    private final ActionCategory category;

    AbstractAction(Path path, ActionCategory category) {
        this.path = requireRelativePath(path);
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AbstractAction that = (AbstractAction) o;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    public Path getPath() {
        return path;
    }

    @Override
    public ActionCategory getActionCategory() {
        return category;
    }
}
