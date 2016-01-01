package nl.ulso.magisto.action;

import nl.ulso.magisto.converter.FileConverter;
import nl.ulso.magisto.io.FileSystem;

import java.io.IOException;
import java.nio.file.Path;

import static nl.ulso.magisto.action.ActionCategory.SOURCE;

/**
 * Converts a file in the source root to a different format in the target root in the same directory.
 */
class ConvertSourceAction extends AbstractAction {

    private final FileConverter fileConverter;

    ConvertSourceAction(Path path, FileConverter fileConverter) {
        super(path, SOURCE);
        this.fileConverter = fileConverter;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.CONVERT_SOURCE;
    }

    @Override
    public void perform(FileSystem fileSystem, Path sourceRoot, Path targetRoot) throws IOException {
        fileConverter.convert(fileSystem, sourceRoot, targetRoot, getPath());
    }
}
