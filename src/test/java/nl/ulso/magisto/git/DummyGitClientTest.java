package nl.ulso.magisto.git;

import org.junit.Test;

import java.util.List;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DummyGitClientTest {

    @Test
    public void testCommitAvailable() throws Exception {
        final History history = new DummyGitClient().getHistory(createPath("file"));
        final Commit commit = history.getLastCommit();
        assertNotNull(commit);
        assertEquals("UNKNOWN", commit.getId());
    }

    @Test
    public void testChangelog() throws Exception {
        final History history = new DummyGitClient().getHistory(createPath("file"));
        final List<Commit> commits = history.getCommits();
        assertNotNull(commits);
        assertTrue(commits.isEmpty());
    }
}