package nl.ulso.magisto.action;

import nl.ulso.magisto.converter.FileConverter;
import nl.ulso.magisto.io.FileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Maintains a set of actions to be performed. On each path at most one action can be performed. Additionally the
 * actions are ordered in a specific order with the {@link ActionComparator}.
 * <p>
 * Two things can be done with an {@code ActionSet}:
 * </p>
 * <ol>
 * <li>Add actions to it, by calling the various {@code add...} methods.</li>
 * <li>Performing all actions in the set, by calling the {@link #performAll(nl.ulso.magisto.io.FileSystem, Path, Path,
 * ActionCallback)} method.</li>
 * </ol>
 * <p>
 * Once all actions are performed, the internal set of actions is depleted.
 * </p>
 */
public class ActionSet {

    private static final ActionComparator ACTION_COMPARATOR = new ActionComparator();

    private final Map<Path, Action> actionMap;
    private final ActionFactory actionFactory;

    public ActionSet(ActionFactory actionFactory) {
        this.actionFactory = actionFactory;
        actionMap = new HashMap<>();
    }

    public void addSkipSourceAction(Path path) {
        add(actionFactory.skipSource(path));
    }

    public void addSkipStaticAction(Path path) {
        add(actionFactory.skipStatic(path));
    }

    public void addCopySourceAction(Path path) {
        add(actionFactory.copySource(path));
    }

    public void addCopyStaticAction(Path path, String staticContentDirectory) {
        add(actionFactory.copyStatic(path, staticContentDirectory));
    }

    public void addConvertSourceAction(Path path, FileConverter fileConverter) {
        add(actionFactory.convertSource(path, fileConverter));
    }

    public void addDeleteTargetAction(Path path) {
        add(actionFactory.deleteTarget(path));
    }

    /**
     * Performs all actions in the list in the right order, calling the callback after each action is performed.
     * Afterwards all actions all cleared from the list, ensuring that the actions in a list can be performed only
     * once.
     *
     * @param fileSystem File system to perform actions in.
     * @param sourceRoot Source root of all actions.
     * @param targetRoot Target root of all actions.
     * @param callback Callback to perform after every action.
     * @throws IOException If anything goes wrong while accessing the file system.
     */
    public void performAll(FileSystem fileSystem, Path sourceRoot, Path targetRoot,
                           ActionCallback callback) throws IOException {
        final SortedSet<Action> actions = new TreeSet<>(ACTION_COMPARATOR);
        actions.addAll(actionMap.values());
        for (Action action : actions) {
            action.perform(fileSystem, sourceRoot, targetRoot);
            callback.actionPerformed(new BlockedActionWrapper(action));
        }
        actions.clear();
    }

    /**
     * Adds an action to the list. If an action for the same path already exists, one may overwrite the other, or
     * cancel both out.
     */
    private void add(Action newAction) {
        final Path path = newAction.getPath();
        final Action oldAction = actionMap.get(path);
        if (oldAction == null) {
            actionMap.put(path, newAction);
            return;
        }
        requireDistinctCategories(oldAction, newAction);
        final Action action = determineAction(oldAction, newAction);
        if (action != null) {
            actionMap.put(path, action);
        } else {
            actionMap.remove(path);
        }
    }

    private void requireDistinctCategories(Action oldAction, Action newAction) {
        if (oldAction.getActionCategory() == newAction.getActionCategory()) {
            throw new IllegalStateException("Two actions for the same path or of the same category");
        }
    }

    private Action determineAction(Action sourceAction, Action staticAction) {
        if (sourceAction.getActionCategory() == ActionCategory.STATIC) {
            // Order is reversed, let's try again:
            return determineAction(staticAction, sourceAction);
        }
        // In all cases except one the SOURCE action overwrites the STATIC action:
        if (sourceAction.getActionType() == ActionType.DELETE_TARGET) {
            return staticAction;
        }
        return sourceAction;
    }

    private class BlockedActionWrapper implements Action {
        private final Action action;

        private BlockedActionWrapper(Action action) {
            this.action = action;
        }

        @Override
        public Path getPath() {
            return action.getPath();
        }

        @Override
        public ActionType getActionType() {
            return action.getActionType();
        }

        @Override
        public void perform(FileSystem fileSystem, Path sourceRoot, Path targetRoot) throws IOException {
            throw new IllegalStateException("Cannot perform an action twice");
        }

        @Override
        public ActionCategory getActionCategory() {
            return action.getActionCategory();
        }
    }
}
