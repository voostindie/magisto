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

import nl.ulso.magisto.action.Action;
import nl.ulso.magisto.action.ActionComparator;
import nl.ulso.magisto.action.ActionFactory;
import nl.ulso.magisto.converter.FileConverter;
import nl.ulso.magisto.converter.FileConverterFactory;
import nl.ulso.magisto.io.FileSystemAccessor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Knits all the components in the Magisto system together (like a module) and runs it.
 */
class Magisto {
    private static final String STATIC_CONTENT_DIRECTORY = ".static";

    private final FileSystemAccessor fileSystemAccessor;
    private final ActionFactory actionFactory;
    private final FileConverterFactory fileConverterFactory;

    public Magisto(FileSystemAccessor fileSystemAccessor, ActionFactory actionFactory,
                   FileConverterFactory fileConverterFactory) {
        this.fileSystemAccessor = fileSystemAccessor;
        this.actionFactory = actionFactory;
        this.fileConverterFactory = fileConverterFactory;
    }

    /*
    Runs Magisto by first determining a set of actions to perform, and then actually performing them, keeping
    statistics along the way.

    The actions are collected and sorted in a specific manner, so that they are performed in the right order. For
    example: deletions from the target first, in reverse order (first files in directories, then the directories
    themselves). Copies go later, in lexicographical order. That's one reasons why collecting actions and performing
    them are two distinct steps. Also, I like this more.
     */
    public Statistics run(final String sourceDirectory, final String targetDirectory) throws IOException {
        final Statistics statistics = new Statistics();
        try {
            statistics.begin();
            final Path sourceRoot = fileSystemAccessor.resolveSourceDirectory(sourceDirectory);
            final Path targetRoot = fileSystemAccessor.prepareTargetDirectory(targetDirectory);
            fileSystemAccessor.requireDistinct(sourceRoot, targetRoot);

            for (Action action : createActions(sourceRoot, targetRoot)) {
                action.perform(fileSystemAccessor, sourceRoot, targetRoot);
                statistics.registerActionPerformed(action);
            }

            // The previous step has removed all static files. The next step copies them back in.
            // The good part: if there's a new file in the source tree with the same name as a static file, then the
            // the static file will not replace it.
            // The bad part: in general static files will be copied over and over again.
            // I still have to think of a solution to fix the bad part...

            final Path staticRoot = sourceRoot.resolve(STATIC_CONTENT_DIRECTORY);
            for (Action action : createStaticCopyActions(staticRoot, targetRoot)) {
                action.perform(fileSystemAccessor, staticRoot, targetRoot);
                statistics.registerActionPerformed(action);
            }

            fileSystemAccessor.writeTouchFile(targetRoot);
        } finally {
            statistics.end();
        }
        return statistics;
    }

    /*
    If it weren't for files that can disappear from the source and must therefore be removed from the target,
    determining the list of actions could be as simple as selecting all files in the source directory that are newer
    than the last export. In that case detecting the files to be deleted would require a separate step after performing
    actions on the source files, to detect all files in the target directory that weren't updated. This balanced line
    algorithm is simpler. It's a bit faster too.
     */
    private SortedSet<Action> createActions(Path sourceRoot, Path targetRoot) throws IOException {
        final SortedSet<Action> actions = new TreeSet<>(new ActionComparator());
        final FileConverter fileConverter = fileConverterFactory.create(fileSystemAccessor, sourceRoot);
        final Iterator<Path> sources = fileSystemAccessor.findAllPaths(sourceRoot).iterator();
        final Iterator<Path> targets = fileSystemAccessor.findAllPaths(targetRoot).iterator();

        Path source = nullableNext(sources);
        Path target = nullableNext(targets);
        while (source != null || target != null) {
            final int comparison = compareNullablePaths(source, target, fileConverter);

            if (comparison == 0) { // Corresponding source and target
                if (isSourceNewerThanTarget(sourceRoot.resolve(source), targetRoot.resolve(target))) {
                    actions.add(determineActionOnSource(source, fileConverter)); // Source is newer, so replace target
                } else {
                    actions.add(actionFactory.skip(source));
                }
                source = nullableNext(sources);
                target = nullableNext(targets);

            } else if (comparison < 0) { // Source exists, no corresponding target
                actions.add(determineActionOnSource(source, fileConverter));
                source = nullableNext(sources);

            } else if (comparison > 0) { // Target exists, no corresponding source
                actions.add(actionFactory.delete(target));
                target = nullableNext(targets);
            }
        }
        return actions;
    }

    private SortedSet<Action> createStaticCopyActions(Path staticRoot, Path targetRoot) throws IOException {
        final SortedSet<Action> actions = new TreeSet<>(new ActionComparator());
        if (fileSystemAccessor.notExists(staticRoot)) {
            return actions;
        }
        final SortedSet<Path> paths = fileSystemAccessor.findAllPaths(staticRoot);
        for (Path path : paths) {
            if (fileSystemAccessor.notExists(targetRoot.resolve(path))) {
                actions.add(actionFactory.copy(path));
            }
        }
        return actions;
    }

    private Path nullableNext(Iterator<Path> paths) {
        return paths.hasNext() ? paths.next() : null;
    }

    private int compareNullablePaths(Path source, Path target, FileConverter fileConverter) {
        if (source == null) {
            return 1;
        }
        if (target == null) {
            return -1;
        }
        if (fileConverter.supports(source)) {
            return fileConverter.getConvertedFileName(source).compareTo(target);
        }
        return source.compareTo(target);
    }

    private Action determineActionOnSource(Path source, FileConverter fileConverter) {
        if (fileConverter.supports(source)) {
            return actionFactory.convert(source, fileConverter);
        }
        return actionFactory.copy(source);
    }

    private boolean isSourceNewerThanTarget(Path sourcePath, Path targetPath) throws IOException {
        final long sourceLastModified = fileSystemAccessor.getLastModifiedInMillis(sourcePath);
        final long targetLastModified = fileSystemAccessor.getLastModifiedInMillis(targetPath);
        return sourceLastModified > targetLastModified;
    }
}
