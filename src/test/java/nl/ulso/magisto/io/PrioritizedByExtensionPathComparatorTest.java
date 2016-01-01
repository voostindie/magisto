package nl.ulso.magisto.io;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Comparator;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertTrue;

public class PrioritizedByExtensionPathComparatorTest {

    private Comparator<Path> comparator;

    @Before
    public void setUp() throws Exception {
        comparator = new PrioritizedByExtensionPathComparator(new String[]{"md", "markdown"});
    }

    @Test
    public void testNonPrioritizedPathsAreSortedInOrder() throws Exception {
        final Path path1 = createPath("src", "main", "java", "Main.java");
        final Path path2 = createPath("src", "test", "java", "MainTest.java");
        assertTrue(comparator.compare(path1, path2) < 0);
    }

    @Test
    public void testPrioritizedPathComesBeforeNormalPath() throws Exception {
        final Path path1 = createPath("file.jpeg");
        final Path path2 = createPath("file.md");
        assertTrue(comparator.compare(path1, path2) > 0);
        assertTrue(comparator.compare(path2, path1) < 0);
    }
}