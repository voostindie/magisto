package nl.ulso.magisto.action;

import nl.ulso.magisto.io.DummyFileSystem;
import nl.ulso.magisto.io.DummyPathEntry;
import org.junit.Test;

import java.nio.file.Path;

import static nl.ulso.magisto.io.DummyPathEntry.createPathEntry;
import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;

public class CopyStaticActionTest {

    @Test
    public void testActionType() throws Exception {
        assertEquals(ActionType.COPY_STATIC, new CopyStaticAction(createPath("copy"), ".").getActionType());
    }

    @Test
    public void testActionCategory() throws Exception {
        assertEquals(ActionCategory.STATIC, new CopyStaticAction(createPath("copy"), ".").getActionCategory());
    }

    @Test
    public void testCopyStatic() throws Exception {
        final DummyFileSystem fileSystem = new DummyFileSystem();
        final Path sourceRoot = fileSystem.resolveSourceDirectory("source");
        final Path targetRoot = fileSystem.prepareTargetDirectory("target");
        final DummyPathEntry entry = createPathEntry("file");
        new CopyStaticAction(entry.getPath(), ".static").perform(fileSystem, sourceRoot, targetRoot);
        assertEquals(".static:file -> target", fileSystem.getLoggedCopies());
    }
}