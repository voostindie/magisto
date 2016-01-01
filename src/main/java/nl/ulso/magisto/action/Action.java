package nl.ulso.magisto.action;

import nl.ulso.magisto.io.FileSystem;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Represents an action to perform on a path
 */
public interface Action {

    Path getPath();

    ActionCategory getActionCategory();

    ActionType getActionType();

    void perform(FileSystem fileSystem, Path sourceRoot, Path targetRoot) throws IOException;
}
