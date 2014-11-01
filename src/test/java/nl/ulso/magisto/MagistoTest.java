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

    private DummyFileSystemAccessor accessor;
    private DummyActionFactory actionFactory;
    private DummyFileConverterFactory fileConverterFactory;
    private Magisto magisto;

    @Before
    public void setUp() throws Exception {
        accessor = new DummyFileSystemAccessor();
        actionFactory = new DummyActionFactory();
        fileConverterFactory = new DummyFileConverterFactory();
        magisto = new Magisto(false, accessor, actionFactory, fileConverterFactory);
    }

    @Test
    public void testEmptySourceEmptyTarget() throws Exception {
        runTest(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void testNormalFileEmptyTargetDirectory() throws Exception {
        accessor.addSourcePaths(createPathEntry("file.jpg"));
        runTest(0, 1, 0, 0, 0, 0);
    }

    @Test
    public void testConversionFileEmptyTargetDirectory() throws Exception {
        accessor.addSourcePaths(createPathEntry("file.convert"));
        runTest(0, 0, 1, 0, 0, 0);
    }

    @Test
    public void testUnknownFileInTargetDirectory() throws Exception {
        accessor.addTargetPaths(createPathEntry("file.jpg"));
        runTest(0, 0, 0, 1, 0, 0);
    }

    @Test
    public void testStaticFileEmptyTargetDirectory() throws Exception {
        accessor.addStaticPaths(createPathEntry("file.jpg"));
        runTest(0, 0, 0, 0, 0, 1);
    }

    @Test
    public void testNormalFilesExistNoChangesDetected() throws Exception {
        final DummyPathEntry file1 = createPathEntry("foo.txt");
        final DummyPathEntry file2 = createPathEntry("bar.jpg");
        accessor.addSourcePaths(file1, file2);
        accessor.addTargetPaths(file1, file2);
        runTest(2, 0, 0, 0, 0, 0);
    }

    @Test
    public void testStaticFilesExistNoChangesDetected() throws Exception {
        final DummyPathEntry file1 = createPathEntry("foo.txt");
        final DummyPathEntry file2 = createPathEntry("bar.jpg");
        accessor.addStaticPaths(file1, file2);
        accessor.addTargetPaths(file1, file2);
        runTest(0, 0, 0, 0, 2, 0);
    }

    @Test
    public void testMultipleSourceAndTargetFiles() throws Exception {
        prepareMultipleSourceAndTargetFiles();
        runTest(
                2, // sameFile1, sameFile2
                1, // baz.txt
                2, // foo.convert/foo.convert.converted, bar.convert
                1, // deleteTarget.me
                1, // .static/favicon.ico
                1  // .static/image.jpg
        );
    }

    @Test
    public void testMultipleSourceAndTargetFilesWithForcedOverwrite() throws Exception {
        magisto = new Magisto(true, accessor, actionFactory, fileConverterFactory);
        prepareMultipleSourceAndTargetFiles();
        runTest(
                0, // no skips: forced overwrite
                3, // sameFile1, sameFile2, baz.txt
                2, // foo.convert/foo.convert.converted, bar.convert
                1, // deleteTarget.me
                0, // no skips: forced overwrite
                2  // .static/favicon.ico, .static/image.jpg
        );
    }

    private void prepareMultipleSourceAndTargetFiles() throws InterruptedException {
        final DummyPathEntry sameSourceFile1 = createPathEntry("foo.txt");
        final DummyPathEntry sameSourceFile2 = createPathEntry("bar.jpg");
        final DummyPathEntry sameStaticFile = createPathEntry("favicon.ico");
        accessor.addTargetPaths(
                sameSourceFile1,
                sameSourceFile2,
                sameStaticFile,
                createPathEntry("baz.txt"),
                createPathEntry("foo.convert.converted"),
                createPathEntry("delete.me")
        );
        TimeUnit.SECONDS.sleep(1);
        accessor.addSourcePaths(
                sameSourceFile1,
                sameSourceFile2,
                createPathEntry("baz.txt"), // Same path, different timestamp: this one is newer
                createPathEntry("foo.convert"),
                createPathEntry("bar.convert")
        );
        accessor.addStaticPaths(
                sameStaticFile,
                createPathEntry("image.jpg")
        );

    }

    private void runTest(int expectedSourceSkips, int expectedSourceCopies, int expectedSourceConversions,
                         int expectedTargetDeletions, int expectedStaticSkips, int expectedStaticCopies)
            throws Exception {
        final Statistics statistics = magisto.run("source", "target");
        // Number of actions performed must match up:
        assertEquals(expectedSourceSkips, actionFactory.countFor(SKIP_SOURCE));
        assertEquals(expectedSourceCopies, actionFactory.countFor(COPY_SOURCE));
        assertEquals(expectedSourceConversions, actionFactory.countFor(CONVERT_SOURCE));
        assertEquals(expectedTargetDeletions, actionFactory.countFor(DELETE_TARGET));
        assertEquals(expectedStaticSkips, actionFactory.countFor(SKIP_STATIC));
        assertEquals(expectedStaticCopies, actionFactory.countFor(COPY_STATIC));
        // Statistics must match the number of actions performed
        assertEquals(expectedSourceSkips, statistics.countFor(SKIP_SOURCE));
        assertEquals(expectedSourceCopies, statistics.countFor(COPY_SOURCE));
        assertEquals(expectedSourceConversions, statistics.countFor(CONVERT_SOURCE));
        assertEquals(expectedTargetDeletions, statistics.countFor(DELETE_TARGET));
        assertEquals(expectedStaticSkips, statistics.countFor(SKIP_STATIC));
        assertEquals(expectedStaticCopies, statistics.countFor(COPY_STATIC));
    }
}
