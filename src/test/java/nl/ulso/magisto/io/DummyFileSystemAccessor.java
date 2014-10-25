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

import java.io.*;
import java.nio.file.Path;
import java.util.*;

import static nl.ulso.magisto.io.Paths.createPath;

public class DummyFileSystemAccessor implements FileSystemAccessor {

    private Recording recording;

    public DummyFileSystemAccessor() {
        clearRecordings();
    }

    public void clearRecordings() {
        recording = new Recording();
    }

    @Override
    public Path resolveSourceDirectory(String directoryName) throws IOException {
        recording.sourceRoot = createPath(directoryName).toAbsolutePath();
        return recording.sourceRoot;
    }

    @Override
    public Path prepareTargetDirectory(String directoryName) throws IOException {
        recording.targetRoot = createPath(directoryName).toAbsolutePath();
        return recording.targetRoot;
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
        if (root.equals(recording.sourceRoot)) {
            addAllPaths(paths, recording.sourcePaths);
        } else if (root.equals(recording.targetRoot)) {
            addAllPaths(paths, recording.targetPaths);
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
        DummyPathEntry sourceEntry = findEntry(recording.sourceRoot.relativize(source), recording.sourcePaths);
        DummyPathEntry targetEntry = findEntry(recording.targetRoot.relativize(target), recording.targetPaths);
        return sourceEntry.getTimestamp() > targetEntry.getTimestamp();
    }

    @Override
    public void copy(Path sourceRoot, Path targetRoot, Path path) throws IOException {
        recording.loggedCopies += String.format("%s:%s -> %s%n",
                sourceRoot.getFileName(), path, targetRoot.getFileName());
    }

    @Override
    public void delete(Path root, Path path) throws IOException {
        recording.loggedDeletions += String.format("%s:%s%n", root.getFileName(), path);
    }

    @Override
    public BufferedReader newBufferedReaderForTextFile(Path path) throws IOException {
        return new BufferedReader(new StringReader(recording.textFilesForReading.get(path.getFileName().toString())));
    }

    @Override
    public BufferedWriter newBufferedWriterForTextFile(Path path) throws IOException {
        final StringWriter writer = new StringWriter();
        recording.textFilesForWriting.put(path.getFileName().toString(), writer);
        return new BufferedWriter(writer);
    }

    private DummyPathEntry findEntry(Path source, Set<DummyPathEntry> entries) {
        for (DummyPathEntry entry : entries) {
            if (entry.getPath().equals(source)) {
                return entry;
            }
        }
        throw new IllegalStateException("Should not get here!");
    }

    public void addSourcePaths(DummyPathEntry... paths) {
        recording.sourcePaths.addAll(Arrays.asList(paths));
    }

    public void addTargetPaths(DummyPathEntry... paths) {
        recording.targetPaths.addAll(Arrays.asList(paths));
    }

    public String getLoggedCopies() {
        return recording.loggedCopies.trim();
    }

    public String getLoggedDeletions() {
        return recording.loggedDeletions.trim();
    }

    public void registerTextFileForBufferedReader(String fileName, String content) {
        recording.textFilesForReading.put(fileName, content);
    }

    public String getTextFileFromBufferedWriter(String fileName) {
        return recording.textFilesForWriting.get(fileName).toString();
    }

    private class Recording {
        private Path sourceRoot = null;
        private Path targetRoot = null;
        private String loggedCopies = "";
        private String loggedDeletions = "";
        private final Set<DummyPathEntry> sourcePaths = new HashSet<>();
        private final Set<DummyPathEntry> targetPaths = new HashSet<>();
        private final Map<String, String> textFilesForReading = new HashMap<>();
        private final Map<String, StringWriter> textFilesForWriting = new HashMap<>();
    }
}
