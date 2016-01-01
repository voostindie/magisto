package nl.ulso.magisto.converter.markdown;

import nl.ulso.magisto.converter.FileConverter;
import nl.ulso.magisto.converter.FileConverterFactory;
import nl.ulso.magisto.git.GitClient;
import nl.ulso.magisto.io.FileSystem;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Default implementation of the {@link nl.ulso.magisto.converter.FileConverterFactory}.
 */
public class MarkdownToHtmlFileConverterFactory implements FileConverterFactory {
    private final GitClient gitClient;

    public MarkdownToHtmlFileConverterFactory(GitClient gitClient) {
        this.gitClient = gitClient;
    }

    @Override
    public FileConverter create(FileSystem fileSystem, Path sourceRoot) throws IOException {
        return new MarkdownToHtmlFileConverter(fileSystem, sourceRoot, gitClient);
    }
}
