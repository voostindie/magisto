package nl.ulso.magisto.action;

import nl.ulso.magisto.converter.DummyFileConverter;
import nl.ulso.magisto.io.DummyFileSystem;
import nl.ulso.magisto.io.DummyPathEntry;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;

import static nl.ulso.magisto.io.DummyPathEntry.createPathEntry;
import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;

public class ConvertSourceActionTest {

    private final DummyFileConverter fileConverter = new DummyFileConverter();

    @Before
    public void setUp() throws Exception {
        fileConverter.clearRecordings();
    }

    @Test
    public void testActionType() throws Exception {
        assertEquals(ActionType.CONVERT_SOURCE,
                new ConvertSourceAction(createPath("convert"), fileConverter).getActionType());
    }

    @Test
    public void testActionCategory() throws Exception {
        assertEquals(ActionCategory.SOURCE,
                new ConvertSourceAction(createPath("convert"), fileConverter).getActionCategory());
    }

    @Test
    public void testCopy() throws Exception {
        final DummyFileSystem fileSystem = new DummyFileSystem();
        final Path sourceRoot = fileSystem.resolveSourceDirectory("source");
        final Path targetRoot = fileSystem.prepareTargetDirectory("target");
        final DummyPathEntry entry = createPathEntry("file.convert");
        new ConvertSourceAction(entry.getPath(), fileConverter).perform(fileSystem, sourceRoot, targetRoot);
        assertEquals("source:file.convert -> target:file.converted", fileConverter.getLoggedConversions());
    }
}