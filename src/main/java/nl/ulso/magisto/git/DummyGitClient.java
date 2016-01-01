package nl.ulso.magisto.git;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * Dummy implementation used then the source directory is not an actual Git repository.
 */
public class DummyGitClient implements GitClient {

    private static final History DEFAULT_HISTORY = new History() {
        @Override
        public List<Commit> getCommits() throws IOException {
            return Collections.emptyList();
        }

        @Override
        public Commit getLastCommit() throws IOException {
            return Commit.DEFAULT_COMMIT;
        }
    };

    @Override
    public History getHistory(Path path) {
        return DEFAULT_HISTORY;
    }
}
