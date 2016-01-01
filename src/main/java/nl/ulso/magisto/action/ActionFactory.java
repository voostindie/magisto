package nl.ulso.magisto.action;

import nl.ulso.magisto.converter.FileConverter;

import java.nio.file.Path;

/**
 * Factory for all the various types of actions.
 */
public interface ActionFactory {

    Action skipSource(Path path);

    Action skipStatic(Path path);

    Action copySource(Path path);

    Action copyStatic(Path path, String staticContentDirectory);

    Action convertSource(Path path, FileConverter fileConverter);

    Action deleteTarget(Path path);
}
