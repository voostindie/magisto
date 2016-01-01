package nl.ulso.magisto.action;

import nl.ulso.magisto.io.DummyFileSystem;
import nl.ulso.magisto.io.DummyPathEntry;
import org.junit.Test;

import java.nio.file.Path;

import static nl.ulso.magisto.io.DummyPathEntry.createPathEntry;
import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;

public class CopySourceActionTest {

    @Test
    public void testActionType() throws Exception {
        assertEquals(ActionType.COPY_SOURCE, new CopySourceAction(createPath("copy")).getActionType());
    }

    @Test
    public void testActionCategory() throws Exception {
        assertEquals(ActionCategory.SOURCE, new CopySourceAction(createPath("copy")).getActionCategory());
    }

    @Test
    public void testCopySource() throws Exception {
        final DummyFileSystem fileSystem = new DummyFileSystem();
        final Path sourceRoot = fileSystem.resolveSourceDirectory("source");
        final Path targetRoot = fileSystem.prepareTargetDirectory("target");
        final DummyPathEntry entry = createPathEntry("file");
        new CopySourceAction(entry.getPath()).perform(fileSystem, sourceRoot, targetRoot);
        assertEquals("source:file -> target", fileSystem.getLoggedCopies());
    }
}