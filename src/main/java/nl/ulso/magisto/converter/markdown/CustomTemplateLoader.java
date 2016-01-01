package nl.ulso.magisto.converter.markdown;

import freemarker.cache.TemplateLoader;
import nl.ulso.magisto.io.FileSystem;
import nl.ulso.magisto.io.Paths;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

/**
 * FreeMarker {@link TemplateLoader} that loads templates from the file system using the {@link nl.ulso.magisto.io.FileSystem}.
 */
class CustomTemplateLoader implements TemplateLoader {

    private final FileSystem fileSystem;
    private final Path root;

    public CustomTemplateLoader(FileSystem fileSystem, Path rootPath) {
        this.fileSystem = fileSystem;
        this.root = rootPath;
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        final String[] parts = name.split("/");
        if (parts.length != 1) {
            throw new UnsupportedOperationException("Template sources in directories are not supported: " + name);
        }
        final Path path = root.resolve(Paths.createPath(parts[0]));
        if (fileSystem.exists(path)) {
            return path;
        }
        return null;
    }

    @Override
    public long getLastModified(Object templateSource) {
        final Path path = (Path) templateSource;
        try {
            return fileSystem.getLastModifiedInMillis(path);
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        final Path path = (Path) templateSource;
        return fileSystem.newBufferedReaderForTextFile(path);
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        // Nothing to do here.
    }
}
