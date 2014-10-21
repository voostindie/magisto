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

package nl.ulso.magisto.io;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Collections;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import static nl.ulso.magisto.io.FileSystemTestRunner.WORKING_DIRECTORY;
import static nl.ulso.magisto.io.FileSystemTestRunner.runFileSystemTest;
import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.*;

public class RealFileSystemAccessorTest {

    private final FileSystemAccessor accessor = new RealFileSystemAccessor();

    @Test
    public void testSourceDirectoryWithJavaCurrentWorkingDirectory() throws Exception {
        final Path expected = WORKING_DIRECTORY;
        final Path actual = accessor.resolveSourceDirectory(System.getProperty("user.dir"));
        assertEquals(expected, actual);
    }

    @Test(expected = NoSuchFileException.class)
    public void testSourceDirectoryExceptionForNonExistingDirectory() throws Exception {
        accessor.resolveSourceDirectory("foo");
    }

    @Test
    public void testResolvedSourceDirectoryIsRealPath() throws Exception {
        final Path expected = WORKING_DIRECTORY.resolve("src");
        final Path actual = accessor.resolveSourceDirectory("src");
        assertEquals(expected.toString(), actual.toString());
    }

    @Test(expected = IOException.class)
    public void testSourceDirectoryIsDirectory() throws Exception {
        accessor.resolveSourceDirectory("pom.xml");
    }

    @Test(expected = IOException.class)
    public void testSourceDirectoryIsReadable() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                // Make the directory unreadable
                Files.setPosixFilePermissions(path, Collections.<PosixFilePermission>emptySet());
            }

            @Override
            public void runTest(Path path) throws IOException {
                // IOException expected, since directory is not readable
                accessor.resolveSourceDirectory(path.toString());
            }
        });
    }

    @Test
    public void testTargetDirectoryWithNonExistingDirectoryIsRealPath() throws Exception {
        runFileSystemTest(new FileSystemTestWithoutTempDirectory() {
            @Override
            public void runTest(Path path) throws IOException {
                final Path actual = accessor.prepareTargetDirectory(path.toString());
                assertEquals(path.toString(), actual.toString());
            }
        });
    }

    @Test
    public void testTargetDirectoryWithExistingDirectoryIsRealPath() throws Exception {
        runFileSystemTest(new FileSystemTestWithEmptyTempDirectory() {
            @Override
            public void runTest(Path path) throws IOException {
                final Path actual = accessor.prepareTargetDirectory(path.toString());
                assertEquals(path.toString(), actual.toString());
            }
        });
    }

    @Test(expected = IOException.class)
    public void testTargetDirectoryIsDirectory() throws Exception {
        accessor.prepareTargetDirectory("pom.xml");
    }

    @Test(expected = IOException.class)
    public void testTargetDirectoryIsWritable() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.setPosixFilePermissions(path, Collections.<PosixFilePermission>emptySet());
            }

            @Override
            public void runTest(Path path) throws IOException {
                accessor.prepareTargetDirectory(path.toString());
            }
        });
    }

    @Test(expected = IOException.class)
    public void testTargetDirectoryMustBeEmpty() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createFile(path.resolve("foo"));
            }

            @Override
            public void runTest(Path path) throws IOException {
                accessor.prepareTargetDirectory(path.toString());
            }
        });
    }

    @Test
    public void testTargetDirectoryMustBeEmptyUnlessItIsAnExport() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createFile(path.resolve("foo"));
                Files.createFile(path.resolve(FileSystemAccessor.MAGISTO_EXPORT_MARKER_FILE));
            }

            @Override
            public void runTest(Path path) throws IOException {
                assertNotNull(accessor.prepareTargetDirectory(path.toString()));
            }
        });
    }

    @Test(expected = IOException.class)
    public void testTargetDirectoryContainsDirectory() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createDirectory(path.resolve("foo"));
            }

            @Override
            public void runTest(Path path) throws IOException {
                assertNotNull(accessor.prepareTargetDirectory(path.toString()));
            }
        });
    }

    @Test
    public void testFindAllPaths() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createFile(path.resolve("foo"));
                Files.createDirectory(path.resolve("bar"));
                Files.createFile(path.resolve("bar").resolve("baz"));
            }

            @Override
            public void runTest(Path path) throws IOException {
                final SortedSet<Path> paths = accessor.findAllPaths(path);
                assertEquals(3, paths.size());
                assertArrayEquals(new Path[]{
                        createPath("bar"),
                        createPath("bar", "baz"),
                        createPath("foo")}, paths.toArray());
            }
        });
    }

    @Test
    public void testSourceAndTargetDoNotOverlap() throws Exception {
        final Path source = WORKING_DIRECTORY.resolve("foo");
        final Path target = WORKING_DIRECTORY.resolve("bar");
        accessor.requireDistinct(source, target);
    }

    @Test(expected = IOException.class)
    public void testSourceInsideTarget() throws Exception {
        final Path source = WORKING_DIRECTORY.resolve("bar").resolve("foo");
        final Path target = WORKING_DIRECTORY.resolve("bar");
        accessor.requireDistinct(source, target);
    }

    @Test(expected = IOException.class)
    public void testTargetInsideSource() throws Exception {
        final Path source = WORKING_DIRECTORY.resolve("foo");
        final Path target = WORKING_DIRECTORY.resolve("foo").resolve("bar");
        accessor.requireDistinct(source, target);
    }

    @Test
    public void testTouchFileIsWritten() throws Exception {
        runFileSystemTest(new FileSystemTestWithEmptyTempDirectory() {
            @Override
            public void runTest(Path path) throws IOException {
                accessor.writeTouchFile(path);
                assertTrue(Files.exists(resolveTouchFile(path)));
            }
        });
    }

    @Test
    public void testTouchFileIsReplaced() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createFile(resolveTouchFile(path));
            }

            @Override
            public void runTest(Path path) throws IOException {
                accessor.writeTouchFile(path);
                assertTrue(Files.exists(resolveTouchFile(path)));
            }
        });
    }

    @Test
    public void testSourceNewerThanTarget() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createFile(path.resolve("target"));
                try {
                    TimeUnit.SECONDS.sleep(2); // Make sure the next file is newer
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                Files.createFile(path.resolve("source"));
            }

            @Override
            public void runTest(Path path) throws IOException {
                assertTrue(accessor.isSourceNewerThanTarget(path.resolve("source"), path.resolve("target")));
                assertFalse(accessor.isSourceNewerThanTarget(path.resolve("target"), path.resolve("source")));
                assertFalse(accessor.isSourceNewerThanTarget(path.resolve("source"), path.resolve("source")));
            }
        });
    }

    @Test
    public void testHiddenFilesAreSkipped() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createDirectory(path.resolve(".tmpdir"));
                Files.createFile(path.resolve(".tmpdir").resolve("file"));
                Files.createFile(path.resolve(".tmpfile"));
            }

            @Override
            public void runTest(Path path) throws IOException {
                assertEquals(0, accessor.findAllPaths(path).size());
            }
        });
    }

    @Test
    public void testPathCopy() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {

            private Path source;
            private Path target;

            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                this.source = path.resolve("source");
                this.target = path.resolve("target");
                Files.createDirectory(source);
                Files.createFile(source.resolve("file"));
                Files.createDirectory(source.resolve("directory"));
                Files.createFile(source.resolve("directory").resolve("file"));
                Files.createDirectory(target);
            }

            @Override
            public void runTest(Path path) throws IOException {
                accessor.copy(source, target, createPath("file"));
                accessor.copy(source, target, createPath("directory"));
                accessor.copy(source, target, createPath("directory").resolve("file"));
                // Bad test, it depends on code that's under test itself:
                assertEquals(3, accessor.findAllPaths(path.resolve("target")).size());
            }
        });
    }

    @Test
    public void testPathDelete() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createFile(path.resolve("file"));
                Files.createDirectory(path.resolve("directory"));
            }

            @Override
            public void runTest(Path path) throws IOException {
                accessor.delete(path, createPath("file"));
                accessor.delete(path, createPath("directory"));
                // Bad test, it depends on code that's under test itself:
                assertEquals(0, accessor.findAllPaths(path).size());
            }
        });
    }

    private Path resolveTouchFile(Path path) {
        return path.resolve(FileSystemAccessor.MAGISTO_EXPORT_MARKER_FILE);
    }
}