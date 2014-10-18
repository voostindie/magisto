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

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation of the {@link nl.ulso.magisto.io.FileSystemAccessor} that actually writes to the file system.
 */
public class RealFileSystemAccessor implements FileSystemAccessor {

    @Override
    public Path resolveSourceDirectory(String directoryName) throws IOException {
        final Path path = FileSystems.getDefault().getPath(requireNonNull(directoryName));
        if (Files.notExists(path)) {
            throw new NoSuchFileException(path.toString());
        }
        if (!Files.isDirectory(path)) {
            throw new IOException("Not a directory: " + path);
        }
        if (!Files.isReadable(path)) {
            throw new IOException("Directory not readable: " + path);
        }
        return path.toRealPath();
    }

    @Override
    public Path prepareTargetDirectory(String directoryName) throws IOException {
        final Path path = FileSystems.getDefault().getPath(requireNonNull(directoryName));
        if (Files.notExists(path)) {
            return Files.createDirectories(path);
        }
        if (!Files.isDirectory(path)) {
            throw new IOException("Not a directory: " + path);
        }
        if (!Files.isWritable(path)) {
            throw new IOException("Directory not writable: " + path);
        }
        final TargetStatus status = new TargetStatus();
        Files.walkFileTree(path, Collections.<FileVisitOption>emptySet(), 1, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (MAGISTO_EXPORT_MARKER_FILE.equals(file.getFileName().toString())) {
                    status.isExport = true;
                    return FileVisitResult.TERMINATE;
                }
                status.hasFiles = true;
                return FileVisitResult.CONTINUE;
            }
        });
        if (status.hasFiles && !status.isExport) {
            throw new IOException("Directory not empty and not an export: " + path);
        }
        return path.toRealPath();
    }

    @Override
    public void requireDistinct(Path sourceDirectory, Path targetDirectory) throws IOException {
        requireAbsolutePath(sourceDirectory);
        requireAbsolutePath(targetDirectory);
        if (targetDirectory.startsWith(sourceDirectory)) {
            throw new IOException("The target directory may not be inside the source directory");
        }
        if (sourceDirectory.startsWith(targetDirectory)) {
            throw new IOException("The source directory may not be inside the target directory");
        }
    }

    @Override
    public void writeTouchFile(Path directory) throws IOException {
        requireAbsolutePath(directory);
        final Path touchFile = directory.resolve(MAGISTO_EXPORT_MARKER_FILE);
        if (Files.exists(touchFile)) {
            Files.delete(touchFile);
        }
        Files.createFile(touchFile);
    }

    private void requireAbsolutePath(Path path) {
        if (!requireNonNull(path).isAbsolute()) {
            throw new IllegalStateException("Not a real path: " + path);
        }
    }

    private static class TargetStatus {
        boolean isExport = false;
        boolean hasFiles = false;
    }
}
