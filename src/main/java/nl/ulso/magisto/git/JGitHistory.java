/*
 * Copyright 2014 Vincent Oostindie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

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

    final Git git;
    final String path;

    JGitHistory(Git git, Path path) {
        this.git = git;
        this.path = path.toString();
    }

    @Override
    public List<Commit> getCommits() throws IOException {
        final List<Commit> changes = new ArrayList<>();
        final Iterator<RevCommit> commits = createCommitIterator();
        while (commits.hasNext()) {
            changes.add(createCommit(commits.next()));
        }
        return unmodifiableList(changes);
    }

    @Override
    public Commit getLastCommit() throws IOException {
        final Iterator<RevCommit> commits = createCommitIterator();
        if (commits.hasNext()) {
            return createCommit(commits.next());
        } else {
            return Commit.DEFAULT_COMMIT;
        }
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
