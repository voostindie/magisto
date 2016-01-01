package nl.ulso.magisto.action;

import nl.ulso.magisto.io.DummyFileSystem;
import nl.ulso.magisto.io.DummyPathEntry;
import org.junit.Test;

import java.nio.file.Path;

import static nl.ulso.magisto.io.DummyPathEntry.createPathEntry;
import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;

public class DeleteTargetActionTest {

    @Test
    public void testActionType() throws Exception {
        assertEquals(ActionType.DELETE_TARGET, new DeleteTargetAction(createPath("delete")).getActionType());
    }

    @Test
    public void testActionCategory() throws Exception {
        assertEquals(ActionCategory.SOURCE, new DeleteTargetAction(createPath("delete")).getActionCategory());
    }

    @Test
    public void testDelete() throws Exception {
        final DummyFileSystem fileSystem = new DummyFileSystem();
        final Path sourceRoot = fileSystem.resolveSourceDirectory("source");
        final Path targetRoot = fileSystem.prepareTargetDirectory("target");
        final DummyPathEntry entry = createPathEntry("file");
        new DeleteTargetAction(entry.getPath()).perform(fileSystem, sourceRoot, targetRoot);
        assertEquals("target:file", fileSystem.getLoggedDeletions());
    }
}