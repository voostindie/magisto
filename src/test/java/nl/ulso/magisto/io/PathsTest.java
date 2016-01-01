package nl.ulso.magisto.io;

import org.junit.Test;

import java.nio.file.Path;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PathsTest {

    @Test
    public void testAbsoluteOk() throws Exception {
        assertNotNull(Paths.requireAbsolutePath(createPath(System.getProperty("user.dir"))));
    }

    @Test(expected = IllegalStateException.class)
    public void testAbsoluteNotOk() throws Exception {
        assertNotNull(Paths.requireAbsolutePath(createPath("src")));
    }

    @Test
    public void testRelativeOk() throws Exception {
        assertNotNull(Paths.requireRelativePath(createPath("src")));
    }

    @Test(expected = IllegalStateException.class)
    public void testRelativeNotOk() throws Exception {
        assertNotNull(Paths.requireRelativePath(createPath(System.getProperty("user.dir"))));
    }

    @Test
    public void testExtensionLessPath() throws Exception {
        final Path path = Paths.createPath("file.txt");
        final ExtensionLessPath extensionLessPath = Paths.splitOnExtension(path);
        assertEquals("file", extensionLessPath.getPathWithoutExtension().toString());
        assertEquals("txt", extensionLessPath.getOriginalExtension());
    }

    @Test
    public void testExtensionLessPathInSubdirectory() throws Exception {
        final Path path = Paths.createPath("path/to/file.txt");
        final ExtensionLessPath extensionLessPath = Paths.splitOnExtension(path);
        assertEquals("path/to/file", extensionLessPath.getPathWithoutExtension().toString());
        assertEquals("txt", extensionLessPath.getOriginalExtension());
    }

    @Test
    public void testExtensionLessPathOnExtensionLessFile() throws Exception {
        final Path path = Paths.createPath("file");
        final ExtensionLessPath extensionLessPath = Paths.splitOnExtension(path);
        assertEquals("file", extensionLessPath.getPathWithoutExtension().toString());
        assertEquals("", extensionLessPath.getOriginalExtension());
    }
}