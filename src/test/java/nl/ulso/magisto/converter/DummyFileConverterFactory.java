package nl.ulso.magisto.converter;

import nl.ulso.magisto.io.FileSystem;

import java.nio.file.Path;

public class DummyFileConverterFactory implements FileConverterFactory {

    private boolean isCustomTemplateChanged = false;

    @Override
    public FileConverter create(FileSystem fileSystem, Path sourceRoot) {
        return new DummyFileConverter(isCustomTemplateChanged);
    }

    public void setCustomTemplateChanged() {
        isCustomTemplateChanged = true;
    }
}
