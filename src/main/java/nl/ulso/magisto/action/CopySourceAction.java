package nl.ulso.magisto.action;

import nl.ulso.magisto.io.FileSystem;

import java.io.IOException;
import java.nio.file.Path;

import static nl.ulso.magisto.action.ActionCategory.SOURCE;
import static nl.ulso.magisto.action.ActionType.COPY_SOURCE;
import static nl.ulso.magisto.io.Paths.requireAbsolutePath;

/**
 * Copies a file from the source root to the target root.
 */
class CopySourceAction extends AbstractAction {

    CopySourceAction(Path path) {
        super(path, SOURCE);
    }

    @Override
    public ActionType getActionType() {
        return COPY_SOURCE;
    }

    @Override
    public void perform(FileSystem fileSystem, Path sourceRoot, Path targetRoot) throws IOException {
        fileSystem.copy(
                requireAbsolutePath(sourceRoot),
                requireAbsolutePath(targetRoot),
                getPath()
        );
    }
}
