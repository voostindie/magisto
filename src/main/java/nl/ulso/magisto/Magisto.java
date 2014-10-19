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
import nl.ulso.magisto.io.FileSystemAccessor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Knits all the components in the Magisto system together (like a module) and runs it.
 */
class Magisto {

    private final Set<String> MARKDOWN_EXTENSIONS = new HashSet<>(Arrays.asList("md", "markdown", "mdown"));

    private final FileSystemAccessor fileSystemAccessor;
    private final ActionFactory actionFactory;

    public Magisto(FileSystemAccessor fileSystemAccessor, ActionFactory actionFactory) {
        this.fileSystemAccessor = fileSystemAccessor;
        this.actionFactory = actionFactory;
    }

    /*
    Runs Magisto by first determining a set of actions to perform, and then actually performing them, keeping
    statistics along the way.

    The actions are collected and sorted in a specific manner, so that they are performed in the right order. For
    example: deletions from the target for, in reverse order (first files in directories, then the directories
    themselves). Copies go later, in lexicographical order. That's one reasons why collecting actions and performing
    them are two distinct steps. Also, I like this better.
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
        final Iterator<Path> sources = fileSystemAccessor.findAllPaths(sourceRoot).iterator();
        final Iterator<Path> targets = fileSystemAccessor.findAllPaths(targetRoot).iterator();

        Path source = nullableNext(sources);
        Path target = nullableNext(targets);
        while (source != null || target != null) {
            final int comparison = compareNullablePaths(source, target);

            if (comparison == 0) { // Corresponding source and target
                if (fileSystemAccessor.isSourceNewerThanTarget(sourceRoot.resolve(source), targetRoot.resolve(target))) {
                    actions.add(determineActionOnSource(source)); // Source is newer, so replace target
                } else {
                    actions.add(actionFactory.skip(source));
                }
                source = nullableNext(sources);
                target = nullableNext(targets);

            } else if (comparison < 0) { // Source exists, no corresponding target
                actions.add(determineActionOnSource(source));
                source = nullableNext(sources);

            } else if (comparison > 0) { // Target exists, no corresponding source
                actions.add(actionFactory.delete(target));
                target = nullableNext(targets);
            }
        }
        return actions;
    }

    private Path nullableNext(Iterator<Path> paths) {
        return paths.hasNext() ? paths.next() : null;
    }

    private int compareNullablePaths(Path source, Path target) {
        if (source == null) {
            return 1;
        }
        if (target == null) {
            return -1;
        }
        // TODO: take into account the rename performed by the conversion process.
        return source.compareTo(target);
    }

    private Action determineActionOnSource(Path source) {
        if (isMarkdownFile(source)) {
            return actionFactory.convert(source);
        }
        return actionFactory.copy(source);
    }

    private boolean isMarkdownFile(Path source) {
        final String fileName = source.getFileName().toString();
        final int position = fileName.lastIndexOf('.');
        if (position < 1) {
            return false;
        }
        if (position == fileName.length()) {
            return false;
        }
        final String extension = fileName.substring(position + 1).toLowerCase();
        return MARKDOWN_EXTENSIONS.contains(extension);
    }
}
