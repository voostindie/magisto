package nl.ulso.magisto.action;

import nl.ulso.magisto.io.FileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import static nl.ulso.magisto.action.ActionCategory.SOURCE;
import static nl.ulso.magisto.action.ActionType.SKIP_SOURCE;

/**
 * Represents a no-op action, for a path that is skipped.
 */
class SkipSourceAction extends AbstractAction {

    SkipSourceAction(Path path) {
        super(path, SOURCE);
    }

    @Override
    public ActionType getActionType() {
        return SKIP_SOURCE;
    }

    @Override
    public void perform(FileSystem fileSystem, Path sourceRoot, Path targetRoot) throws IOException {
        Logger.getGlobal().log(Level.FINE, String.format("Skipping source '%s'. No changes detected.", getPath()));
    }
}
