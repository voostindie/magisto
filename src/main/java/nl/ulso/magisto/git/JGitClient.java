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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.Iterator;

/**
 * {@link GitClient} implementation based on JGit.
 */
public class JGitClient implements GitClient {

    private final Git git;

    public JGitClient(String sourceDirectory) throws IOException {
        git = Git.open(new File(sourceDirectory));
    }

    @Override
    public Commit getLastCommit(Path path) throws IOException {
        try {
            final Iterator<RevCommit> commits = git.log().addPath(path.toString()).call().iterator();
            if (commits.hasNext()) {
                return createCommit(commits.next());
            } else {
                return Commit.DEFAULT_COMMIT;
            }
        } catch (GitAPIException e) {
            throw new IOException(e);
        }
    }

    private Commit createCommit(RevCommit revCommit) {
        return new Commit(
                revCommit.getId().name(),
                new Date((long)revCommit.getCommitTime() * 1000l),
                revCommit.getCommitterIdent().getName(),
                revCommit.getCommitterIdent().getEmailAddress(),
                revCommit.getShortMessage());
    }
}
