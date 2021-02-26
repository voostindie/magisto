package nl.ulso.magisto.git;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.junit.Test;

import java.nio.file.Files;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.*;

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

    @Test
    public void testSeparateGitDir() throws Exception {
        var tmpDir = Files.createTempDirectory("magisto");
        var gitConfig = tmpDir.resolve(".git");
        Files.writeString(gitConfig, "gitdir: /tmp/fake-git-dir");
        try {
            new JGitClient(tmpDir.toString());
            fail("This should fail with a RepositoryNotFoundException!");
        } catch (RepositoryNotFoundException e) {
            assertTrue(e.getMessage().endsWith("/tmp/fake-git-dir"));
        } finally {
            Files.delete(gitConfig);
            Files.delete(tmpDir);
        }
    }
}