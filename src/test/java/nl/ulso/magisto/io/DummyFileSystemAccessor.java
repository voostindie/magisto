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
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;

public class DummyFileSystemAccessor implements FileSystemAccessor {

    private Path sourceRoot;
    private Path targetRoot;
    private String loggedCopies = "";
    private String loggedDeletions = "";
    private final Set<DummyPathEntry> sourcePaths = new HashSet<>();
    private final Set<DummyPathEntry> targetPaths = new HashSet<>();

    @Override
    public Path resolveSourceDirectory(String directoryName) throws IOException {
        sourceRoot = FileSystems.getDefault().getPath(directoryName);
        return sourceRoot;
    }

    @Override
    public Path prepareTargetDirectory(String directoryName) throws IOException {
        targetRoot = FileSystems.getDefault().getPath(directoryName);
        return targetRoot;
    }

    @Override
    public void requireDistinct(Path sourceDirectory, Path targetDirectory) throws IOException {
    }

    @Override
    public void writeTouchFile(Path directory) throws IOException {
    }

    @Override
    public SortedSet<Path> findAllPaths(Path root) throws IOException {
        final SortedSet<Path> paths = new TreeSet<>();
        if (root.equals(sourceRoot)) {
            addAllPaths(paths, sourcePaths);
        } else if (root.equals(targetRoot)) {
            addAllPaths(paths, targetPaths);
        }
        return paths;
    }

    private void addAllPaths(SortedSet<Path> paths, Set<DummyPathEntry> entries) {
        for (DummyPathEntry entry : entries) {
            paths.add(entry.getPath());
        }
    }

    @Override
    public boolean isSourceNewerThanTarget(Path source, Path target) throws IOException {
        DummyPathEntry sourceEntry = findEntry(sourceRoot.relativize(source), sourcePaths);
        DummyPathEntry targetEntry = findEntry(targetRoot.relativize(target), targetPaths);
        return sourceEntry.getTimestamp() > targetEntry.getTimestamp();
    }

    @Override
    public void copy(Path sourceRoot, Path targetRoot, Path path) throws IOException {
        loggedCopies += String.format("%s:%s -> %s%n", sourceRoot, path, targetRoot);
    }

    @Override
    public void delete(Path root, Path path) throws IOException {
        loggedDeletions += String.format("%s:%s%n", root, path);
    }

    private DummyPathEntry findEntry(Path source, Set<DummyPathEntry> entries) {
        for (DummyPathEntry entry : entries) {
            if (entry.getPath().equals(source)) {
                return entry;
            }
        }
        throw new IllegalStateException("Should not get here!");
    }

    public void clearRecordings() {
        sourcePaths.clear();
        targetPaths.clear();
        loggedCopies = "";
        loggedDeletions = "";
    }

    public void addSourcePaths(DummyPathEntry... paths) {
        sourcePaths.addAll(Arrays.asList(paths));
    }

    public void addTargetPaths(DummyPathEntry... paths) {
        targetPaths.addAll(Arrays.asList(paths));
    }

    public String getLoggedCopies() {
        return loggedCopies.trim();
    }

    public String getLoggedDeletions() {
        return loggedDeletions.trim();
    }
}
