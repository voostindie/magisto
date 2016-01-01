package nl.ulso.magisto.action;

import nl.ulso.magisto.io.FileSystem;

import java.io.IOException;
import java.nio.file.Path;

import static nl.ulso.magisto.action.ActionCategory.STATIC;
import static nl.ulso.magisto.action.ActionType.COPY_STATIC;
import static nl.ulso.magisto.io.Paths.requireAbsolutePath;

/**
 * Copies a file from the static root to the target root.
 */
class CopyStaticAction extends AbstractAction {

    private final String staticContentDirectory;

    CopyStaticAction(Path path, String staticContentDirectory) {
        super(path, STATIC);
        this.staticContentDirectory = staticContentDirectory;
    }

    @Override
    public ActionType getActionType() {
        return COPY_STATIC;
    }

    @Override
    public void perform(FileSystem fileSystem, Path sourceRoot, Path targetRoot) throws IOException {
        fileSystem.copy(
                requireAbsolutePath(sourceRoot).resolve(staticContentDirectory),
                requireAbsolutePath(targetRoot),
                getPath()
        );
    }
}
