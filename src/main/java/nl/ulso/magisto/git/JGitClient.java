package nl.ulso.magisto.git;

import org.eclipse.jgit.api.Git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * {@link GitClient} implementation based on JGit.
 */
public class JGitClient implements GitClient {

    private final Git git;

    public JGitClient(String sourceDirectory) throws IOException {
        git = Git.open(new File(sourceDirectory));
    }

    @Override
    /*
     * The logic to actually go out into the history is in the history itself. This way it's only performed if the page
     * template is actually accessing it.
     */
    public History getHistory(Path path) {
        return new JGitHistory(git, path);
    }
}
