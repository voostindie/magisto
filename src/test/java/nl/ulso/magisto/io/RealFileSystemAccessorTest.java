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

import static nl.ulso.magisto.io.FileSystemTestRunner.WORKING_DIRECTORY;
import static nl.ulso.magisto.io.FileSystemTestRunner.runFileSystemTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

    private Path resolveTouchFile(Path path) {
        return path.resolve(FileSystemAccessor.MAGISTO_EXPORT_MARKER_FILE);
    }
}