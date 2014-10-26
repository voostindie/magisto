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
import nl.ulso.magisto.converter.DummyFileConverterFactory;
import nl.ulso.magisto.io.DummyFileSystemAccessor;
import nl.ulso.magisto.io.DummyPathEntry;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static nl.ulso.magisto.action.ActionType.*;
import static nl.ulso.magisto.io.DummyPathEntry.createPathEntry;
import static org.junit.Assert.assertEquals;

public class MagistoTest {

    private final DummyFileSystemAccessor accessor = new DummyFileSystemAccessor();
    private final DummyActionFactory actionFactory = new DummyActionFactory();
    private final DummyFileConverterFactory fileConverterFactory = new DummyFileConverterFactory();
    private final Magisto magisto = new Magisto(accessor, actionFactory, fileConverterFactory);

    @Before
    public void setUp() throws Exception {
        accessor.clearRecordings();
        actionFactory.clearRecordings();
    }

    @Test
    public void testEmptySourceEmptyTarget() throws Exception {
        runTest(0, 0, 0, 0);
    }

    @Test
    public void testNormalFileEmptyTargetDirectory() throws Exception {
        accessor.addSourcePaths(createPathEntry("file.jpg"));
        runTest(0, 1, 0, 0);
    }

    @Test
    public void testConversionFileEmptyTargetDirectory() throws Exception {
        accessor.addSourcePaths(createPathEntry("file.convert"));
        runTest(0, 0, 1, 0);
    }

    @Test
    public void testUnknownFileInTargetDirectory() throws Exception {
        accessor.addTargetPaths(createPathEntry("file.jpg"));
        runTest(0, 0, 0, 1);
    }

    @Test
    public void testFilesExistNoChangesDetected() throws Exception {
        final DummyPathEntry file1 = createPathEntry("foo.txt");
        final DummyPathEntry file2 = createPathEntry("bar.jpg");
        accessor.addSourcePaths(file1, file2);
        accessor.addTargetPaths(file1, file2);
        runTest(2, 0, 0, 0);
    }

    @Test
    public void testMultipleSourceAndTargetFiles() throws Exception {
        final DummyPathEntry sameFile1 = createPathEntry("foo.txt");
        final DummyPathEntry sameFile2 = createPathEntry("bar.jpg");
        accessor.addTargetPaths(
                sameFile1,
                sameFile2,
                createPathEntry("baz.txt"),
                createPathEntry("foo.convert.converted"),
                createPathEntry("delete.me")
        );
        TimeUnit.SECONDS.sleep(1);
        accessor.addSourcePaths(
                sameFile1,
                sameFile2,
                createPathEntry("baz.txt"), // Same path, different timestamp: this one is newer
                createPathEntry("foo.convert"),
                createPathEntry("bar.convert")
        );
        runTest(
                2, // sameFile1, sameFile2
                1, // baz.txt
                2, // foo.convert/foo.convert.converted, bar.convert
                1  // deleteTarget.me
        );
    }

    private void runTest(int expectedSkips, int expectedCopies, int expectedConversions, int expectedDeletions) throws Exception {
        final Statistics statistics = magisto.run("source", "target");
        // Number of actions performed must match up:
        assertEquals(expectedSkips, actionFactory.countFor(SKIP_SOURCE));
        assertEquals(expectedCopies, actionFactory.countFor(COPY_SOURCE));
        assertEquals(expectedConversions, actionFactory.countFor(CONVERT_SOURCE));
        assertEquals(expectedDeletions, actionFactory.countFor(DELETE_TARGET));
        // Statistics must match the number of actions performed
        assertEquals(expectedSkips, statistics.countFor(SKIP_SOURCE));
        assertEquals(expectedCopies, statistics.countFor(COPY_SOURCE));
        assertEquals(expectedConversions, statistics.countFor(CONVERT_SOURCE));
        assertEquals(expectedDeletions, statistics.countFor(DELETE_TARGET));
    }
}
