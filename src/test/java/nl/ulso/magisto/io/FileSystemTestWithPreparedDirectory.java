package nl.ulso.magisto.io;

abstract class FileSystemTestWithPreparedDirectory implements FileSystemTest {
    @Override
    public boolean mustCreateTempDirectory() {
        return true;
    }
}
