package nl.ulso.magisto.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.unmodifiableList;

class JGitHistory implements History {

    private final Git git;
    private final String path;

    private Commit lastCommit;
    private List<Commit> commits;

    JGitHistory(Git git, Path path) {
        this.git = git;
        this.path = path.toString();
        this.lastCommit = null;
        this.commits = null;
    }

    @Override
    public List<Commit> getCommits() throws IOException {
        if (commits == null) {
            commits = new ArrayList<>();
            final Iterator<RevCommit> iterator = createCommitIterator();
            while (iterator.hasNext()) {
                commits.add(createCommit(iterator.next()));
            }
        }
        return unmodifiableList(commits);
    }

    @Override
    public Commit getLastCommit() throws IOException {
        if (lastCommit == null) {
            final Iterator<RevCommit> commits = createCommitIterator();
            if (commits.hasNext()) {
                lastCommit = createCommit(commits.next());
            } else {
                lastCommit = Commit.DEFAULT_COMMIT;
            }
        }
        return lastCommit;
    }

    private Iterator<RevCommit> createCommitIterator() throws IOException {
        final Iterator<RevCommit> commits;
        try {
            commits = git.log().addPath(path).call().iterator();
        } catch (GitAPIException e) {
            throw new IOException(e);
        }
        return commits;
    }

    private Commit createCommit(RevCommit revCommit) {
        return new Commit(
                revCommit.getId().name(),
                new Date((long) revCommit.getCommitTime() * 1000l),
                revCommit.getCommitterIdent().getName(),
                revCommit.getCommitterIdent().getEmailAddress(),
                revCommit.getShortMessage(),
                revCommit.getFullMessage());
    }
}
