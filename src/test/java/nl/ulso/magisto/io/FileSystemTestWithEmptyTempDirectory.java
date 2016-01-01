package nl.ulso.magisto.io;

import java.io.IOException;
import java.nio.file.Path;

abstract class FileSystemTestWithEmptyTempDirectory implements FileSystemTest {
    @Override
    public boolean mustCreateTempDirectory() {
        return true;
    }

    @Override
    public void prepareTempDirectory(Path path) throws IOException {
    }
}
