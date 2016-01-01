package nl.ulso.magisto.action;

import nl.ulso.magisto.io.FileSystem;

import java.nio.file.Path;

public class DummyAction implements Action {

    private final DummyActionFactory factory;
    private final Path path;
    private final ActionCategory category;
    private final ActionType type;

    public DummyAction(DummyActionFactory factory, Path path, ActionCategory category, ActionType type) {
        this.factory = factory;
        this.path = path;
        this.category = category;
        this.type = type;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public ActionCategory getActionCategory() {
        return category;
    }

    @Override
    public ActionType getActionType() {
        return type;
    }

    @Override
    public void perform(FileSystem fileSystem, Path sourceRoot, Path targetRoot) {
        factory.registerActionPerformed(this);
    }

    @Override
    public String toString() {
        return String.format("%s: %s", type, path);
    }
}
