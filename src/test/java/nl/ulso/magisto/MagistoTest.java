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

package nl.ulso.magisto;

import nl.ulso.magisto.action.DummyActionFactory;
import nl.ulso.magisto.io.DummyFileSystemAccessor;
import nl.ulso.magisto.io.DummyPathEntry;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static nl.ulso.magisto.action.ActionType.*;
import static nl.ulso.magisto.action.DummyAction.*;
import static nl.ulso.magisto.io.DummyPathEntry.createPath;
import static org.junit.Assert.assertEquals;

public class MagistoTest {

    private final DummyFileSystemAccessor accessor = new DummyFileSystemAccessor();
    private final DummyActionFactory actionFactory = new DummyActionFactory();
    private final Magisto magisto = new Magisto(accessor, actionFactory);

    @Before
    public void setUp() throws Exception {
        accessor.clearRecordings();
        actionFactory.clearCounts();
    }

    @Test
    public void testEmptySourceEmptyTarget() throws Exception {
        runTest(0, 0, 0);
    }

    @Test
    public void testNormalFileEmptyTargetDirectory() throws Exception {
        accessor.addSourcePaths(createPath("file.jpg"));
        runTest(1, 0, 0);
    }

    @Test
    public void testMarkdownFileEmptyTargetDirectory() throws Exception {
        accessor.addSourcePaths(createPath("file.md"));
        runTest(0, 1, 0);
    }

    @Test
    public void testDotFileEmptyTargetDirectory() throws Exception {
        accessor.addSourcePaths(createPath(".md")); // Normal file, not a Markdown file!
        runTest(1, 0, 0);
    }

    @Test
    public void testMarkdownExtensionsEmptyTargetDirectory() throws Exception {
        accessor.addSourcePaths(createPath("foo.md"), createPath("bar.mdown"), createPath("baz.markdown"));
        runTest(0, 3, 0);
    }

    @Test
    public void testFilesExistNoChangesDetected() throws Exception {
        final DummyPathEntry file1 = createPath("foo.md");
        final DummyPathEntry file2 = createPath("bar.jpg");
        accessor.addSourcePaths(file1, file2);
        accessor.addTargetPaths(file1, file2);
        runTest(0, 0, 0);
    }

    @Test
    public void testUnknownFileInTargetDirectory() throws Exception {
        accessor.addTargetPaths(createPath("file.jpg"));
        runTest(0, 0, 1);
    }

    @Test
    public void testMultipleSourceAndTargetFiles() throws Exception {
        final DummyPathEntry sameFile1 = createPath("foo.md");
        final DummyPathEntry sameFile2 = createPath("bar.jpg");
        accessor.addTargetPaths(
                sameFile1,
                sameFile2,
                createPath("baz.txt"),
                createPath("delete.me")
        );
        TimeUnit.SECONDS.sleep(1);
        accessor.addSourcePaths(
                sameFile1,
                sameFile2,
                createPath("baz.txt") /* Same path, different timestamp: this one is newer */,
                createPath("bar.md")
        );
        runTest(1 /* baz.txt */, 1 /* bar.md */, 1 /* delete.me */);
    }

    private void runTest(int expectedCopies, int expectedConversions, int expectedDeletions) throws Exception {
        final Statistics statistics = magisto.run("source", "target");
        // Number of actions performed must match up:
        assertEquals(expectedCopies, COPY_ACTION.getCount());
        assertEquals(expectedConversions, CONVERT_ACTION.getCount());
        assertEquals(expectedDeletions, DELETE_ACTION.getCount());
        // Statistics must match the number of actions performed
        assertEquals(expectedCopies, statistics.countFor(COPY));
        assertEquals(expectedConversions, statistics.countFor(CONVERT));
        assertEquals(expectedDeletions, statistics.countFor(DELETE));
    }
}
