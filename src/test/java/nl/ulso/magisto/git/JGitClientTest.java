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

import org.junit.Test;

import java.util.List;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JGitClientTest {

    @Test
    public void testCommitAvailable() throws Exception {
        final Commit commit = new JGitClient(System.getProperty("user.dir")).getLastCommit(createPath("pom.xml"));
        assertNotNull(commit);
    }

    @Test
    public void testChangelog() throws Exception {
        final List<Commit> changelog = new JGitClient(System.getProperty("user.dir")).getChangelog();
        assertEquals(30, changelog.size());
    }
}