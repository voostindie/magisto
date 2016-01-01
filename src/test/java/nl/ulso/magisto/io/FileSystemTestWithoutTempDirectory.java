package nl.ulso.magisto.io;

import java.io.IOException;
import java.nio.file.Path;

abstract class FileSystemTestWithoutTempDirectory implements FileSystemTest {
    @Override
    public boolean mustCreateTempDirectory() {
        return false;
    }

    @Override
    public void prepareTempDirectory(Path path) throws IOException {
    }
}
