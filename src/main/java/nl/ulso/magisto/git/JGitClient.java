package nl.ulso.magisto.git;

import org.eclipse.jgit.api.Git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * {@link GitClient} implementation based on JGit.
 */
public class JGitClient implements GitClient {

    private static final String GITDIR_KEY = "gitdir: ";

    private final Git git;

    public JGitClient(String sourceDirectory) throws IOException {
        Optional<String> gitDir = resolveGitDirectory(sourceDirectory);
        git = Git.open(new File(gitDir.orElse(sourceDirectory)));
    }

    /**
     * Resolves the Git directory from the source directory. Either this is the directory itself, or, in case of
     * a work directory with a Git directory elsewhere, that Git directory. This is the case when the directory
     * contains a file named <code>.git</code>, with a property <code>gitdir:</code> in it.
     */
    private Optional<String> resolveGitDirectory(String sourceDirectory) throws IOException {
        var gitDir = Optional.<String>empty();
        var gitConfig = Paths.get(sourceDirectory, ".git");
        if (Files.isRegularFile(gitConfig)) {
            gitDir = Files.readAllLines(gitConfig).stream()
                    .filter(s -> s.startsWith(GITDIR_KEY))
                    .map(s -> s.substring(GITDIR_KEY.length()).trim())
                    .findFirst();
        }
        return gitDir;
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
