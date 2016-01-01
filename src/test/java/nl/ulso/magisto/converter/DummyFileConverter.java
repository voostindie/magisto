package nl.ulso.magisto.converter;

import nl.ulso.magisto.io.FileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DummyFileConverter implements FileConverter {

    private final boolean isCustomTemplateChanged;
    private String loggedConversions = "";

    public DummyFileConverter() {
        this(false);
    }

    public DummyFileConverter(boolean isCustomTemplateChanged) {
        this.isCustomTemplateChanged = isCustomTemplateChanged;
    }

    @Override
    public Set<String> getSourceExtensions() {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList("convert")));
    }

    @Override
    public String getTargetExtension() {
        return "converted";
    }

    @Override
    public boolean supports(Path path) {
        return path.getFileName().toString().endsWith(".convert");
    }

    @Override
    public Path getConvertedFileName(Path path) {
        return path.resolveSibling(path.getFileName().toString() + "ed");
    }

    @Override
    public void convert(FileSystem fileSystem, Path sourceRoot, Path targetRoot, Path path)
            throws IOException {
        loggedConversions += String.format("%s:%s -> %s:%s", sourceRoot.getFileName(), path.getFileName(),
                targetRoot.getFileName(), getConvertedFileName(path).getFileName());
    }

    @Override
    public boolean isCustomTemplateChanged(FileSystem fileSystem, Path sourceRoot, Path targetRoot)
            throws IOException {
        return isCustomTemplateChanged;
    }

    public String getLoggedConversions() {
        return loggedConversions;
    }

    public void clearRecordings() {
        loggedConversions = "";
    }
}
