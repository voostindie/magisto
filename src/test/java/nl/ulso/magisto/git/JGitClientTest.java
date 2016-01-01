package nl.ulso.magisto.git;

import org.junit.Test;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JGitClientTest {

    @Test
    public void testCommitAvailable() throws Exception {
        final History history = new JGitClient(System.getProperty("user.dir")).getHistory(createPath("pom.xml"));
        assertNotNull(history);
        assertNotNull(history.getLastCommit());
    }

    @Test
    public void testChangelog() throws Exception {
        final History history = new JGitClient(System.getProperty("user.dir")).getHistory(createPath("pom.xml"));
        assertNotNull(history);
        assertNotNull(history.getCommits());
        assertTrue(history.getCommits().size() > 0);
    }
}