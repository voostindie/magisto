package nl.ulso.magisto.io;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

import static nl.ulso.magisto.io.DummyPathEntry.createPathEntry;
import static nl.ulso.magisto.io.Paths.createPath;

public class DummyFileSystem implements FileSystem {

    private final Path sourceRoot;
    private final Path staticRoot;
    private final Path targetRoot;
    private final Set<DummyPathEntry> sourcePaths = new HashSet<>();
    private final Set<DummyPathEntry> staticPaths = new HashSet<>();
    private final Set<DummyPathEntry> targetPaths = new HashSet<>();
    private final Map<String, String> textFilesForReading = new HashMap<>();
    private final Map<String, StringWriter> textFilesForWriting = new HashMap<>();
    private String loggedCopies = "";
    private String loggedDeletions = "";
    private long touchFileTimestamp = -1;

    public DummyFileSystem() {
        this.sourceRoot = createPath("source").toAbsolutePath();
        this.staticRoot = this.sourceRoot.resolve(".static");
        this.targetRoot = createPath("target").toAbsolutePath();
    }

    @Override
    public Path resolveSourceDirectory(String directoryName) throws IOException {
        return sourceRoot;
    }

    @Override
    public Path prepareTargetDirectory(String directoryName) throws IOException {
        return targetRoot;
    }

    @Override
    public void requireDistinct(Path sourceRoot, Path targetRoot) throws IOException {
    }

    @Override
    public void writeTouchFile(Path targetRoot) throws IOException {
    }

    @Override
    public long getTouchFileLastModifiedInMillis(Path targetRoot) throws IOException {
        return touchFileTimestamp;
    }

    @Override
    public SortedSet<Path> findAllPaths(Path root) throws IOException {
        return findAllPaths(root, new Comparator<Path>() {
            @Override
            public int compare(Path o1, Path o2) {
                return o1.compareTo(o2);
            }
        });
    }

    @Override
    public SortedSet<Path> findAllPaths(Path root, Comparator<? super Path> comparator) throws IOException {
        final SortedSet<Path> paths = new TreeSet<>(comparator);
        if (root.equals(sourceRoot)) {
            addAllPaths(paths, sourcePaths);
        } else if (root.equals(staticRoot)) {
            addAllPaths(paths, staticPaths);
        } else if (root.equals(targetRoot)) {
            addAllPaths(paths, targetPaths);
        }
        return paths;
    }

    @Override
    public long getLastModifiedInMillis(Path path) throws IOException {
        final DummyPathEntry entry;
        if (path.startsWith(staticRoot)) {
            entry = findEntry(staticRoot.relativize(path), staticPaths);
        } else if (path.startsWith(sourceRoot)) {
            entry = findEntry(sourceRoot.relativize(path), sourcePaths);
        } else if (path.startsWith(targetRoot)) {
            entry = findEntry(targetRoot.relativize(path), targetPaths);
        } else {
            entry = null;
        }
        return entry != null ? entry.getTimestamp() : -1;
    }

    private void addAllPaths(SortedSet<Path> paths, Set<DummyPathEntry> entries) {
        for (DummyPathEntry entry : entries) {
            paths.add(entry.getPath());
        }
    }

    @Override
    public void copy(Path sourceRoot, Path targetRoot, Path path) throws IOException {
        loggedCopies += String.format("%s:%s -> %s%n", sourceRoot.getFileName(), path, targetRoot.getFileName());
    }

    @Override
    public void delete(Path root, Path path) throws IOException {
        loggedDeletions += String.format("%s:%s%n", root.getFileName(), path);
    }

    @Override
    public BufferedReader newBufferedReaderForTextFile(Path path) throws IOException {
        return new BufferedReader(new StringReader(textFilesForReading.get(path.getFileName().toString())));
    }

    @Override
    public BufferedWriter newBufferedWriterForTextFile(Path path) throws IOException {
        final StringWriter writer = new StringWriter();
        textFilesForWriting.put(path.getFileName().toString(), writer);
        return new BufferedWriter(writer);
    }

    @Override
    public boolean exists(Path path) {
        if (path.equals(staticRoot)) {
            return !staticPaths.isEmpty();
        }
        if (path.startsWith(targetRoot)) {
            return targetPaths.contains(createPathEntry(path));
        }
        return staticPaths.contains(createPathEntry(path))
                || textFilesForReading.containsKey(path.getFileName().toString());
    }

    @Override
    public boolean notExists(Path path) {
        return !exists(path);
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
        sourcePaths.addAll(Arrays.asList(paths));
    }

    public void addStaticPaths(DummyPathEntry... paths) {
        staticPaths.addAll(Arrays.asList(paths));
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

    public void registerTextFileForBufferedReader(String fileName, String content) {
        textFilesForReading.put(fileName, content);
    }

    public String getTextFileFromBufferedWriter(String fileName) {
        return textFilesForWriting.get(fileName).toString();
    }

    public void markTouchFile() {
        touchFileTimestamp = System.currentTimeMillis() - 1000;
    }
}
