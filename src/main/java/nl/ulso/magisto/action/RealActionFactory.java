package nl.ulso.magisto.action;

import nl.ulso.magisto.converter.FileConverter;

import java.nio.file.Path;

/**
 * Real implementation of the {@link ActionFactory}.
 */
public class RealActionFactory implements ActionFactory {
    @Override
    public Action skipSource(Path path) {
        return new SkipSourceAction(path);
    }

    @Override
    public Action skipStatic(Path path) {
        return new SkipStaticAction(path);
    }

    @Override
    public Action copySource(Path path) {
        return new CopySourceAction(path);
    }

    @Override
    public Action copyStatic(Path path, String staticContentDirectory) {
        return new CopyStaticAction(path, staticContentDirectory);
    }

    @Override
    public Action convertSource(Path path, FileConverter fileConverter) {
        return new ConvertSourceAction(path, fileConverter);
    }

    @Override
    public Action deleteTarget(Path path) {
        return new DeleteTargetAction(path);
    }
}