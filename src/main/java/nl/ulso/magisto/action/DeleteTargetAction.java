package nl.ulso.magisto.action;

import nl.ulso.magisto.io.FileSystem;

import java.io.IOException;
import java.nio.file.Path;

import static nl.ulso.magisto.action.ActionCategory.SOURCE;
import static nl.ulso.magisto.action.ActionType.DELETE_TARGET;

/**
 * Deletes a file or directory from the target root
 */
class DeleteTargetAction extends AbstractAction {

    DeleteTargetAction(Path path) {
        super(path, SOURCE);
    }

    @Override
    public ActionType getActionType() {
        return DELETE_TARGET;
    }

    @Override
    public void perform(FileSystem fileSystem, Path sourceRoot, Path targetRoot) throws IOException {
        fileSystem.delete(targetRoot, getPath());
    }
}
