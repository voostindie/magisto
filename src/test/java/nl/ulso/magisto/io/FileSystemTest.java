package nl.ulso.magisto.io;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Represents a test case that is executed in a temporary directory. Implementations needn't care about creating the
 * temporary directory, nor about cleaning it up.
 *
 * @see nl.ulso.magisto.io.FileSystemTestRunner
 */
interface FileSystemTest {
    boolean mustCreateTempDirectory();

    void prepareTempDirectory(Path path) throws IOException;

    void runTest(Path path) throws IOException;
}
