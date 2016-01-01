package nl.ulso.magisto.action;

import nl.ulso.magisto.io.FileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import static java.util.logging.Level.FINE;
import static nl.ulso.magisto.action.ActionCategory.STATIC;
import static nl.ulso.magisto.action.ActionType.SKIP_STATIC;

/**
 * Represents a no-op action, for a path that is skipped.
 */
class SkipStaticAction extends AbstractAction {

    SkipStaticAction(Path path) {
        super(path, STATIC);
    }

    @Override
    public ActionType getActionType() {
        return SKIP_STATIC;
    }

    @Override
    public void perform(FileSystem fileSystem, Path sourceRoot, Path targetRoot) throws IOException {
        Logger.getGlobal().log(FINE, String.format("Skipping static '%s'. No changes detected.", getPath()));
    }
}
