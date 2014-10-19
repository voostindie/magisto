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
import java.nio.file.Path;
import java.util.SortedSet;

/**
 * Handles all file system access done by Magisto.
 *
 * Yet another filesystem abstraction? Really?
 *
 * Magisto is doing a lot of file system access: reading files, loading template files, copying files, reading and
 * writing directories... All file system access is here, nicely isolated, so that it can easily be swapped out
 * in unit tests.
 */
public interface FileSystemAccessor {

    /**
     * Name of the empty file to write after every export.
     */
    static final String MAGISTO_EXPORT_MARKER_FILE = ".magisto-export";

    /**
     * Resolves and checks the source directory.
     * <p>
     * If the directory is not according to the rules, this method throws an {@link IOException}. The rules are:
     * </p>
     * <ul>
     * <li>It must exist</li>
     * <li>It must be a directory</li>
     * <li>It must be readable</li>
     * </li>
     * </ul>
     * <p>
     * The path returned is an absolute path.
     * </p>
     *
     * @param directoryName Name of the source directory.
     * @return Existing, valid, real path to the directory.
     * @throws IOException if the path couldn't be resolved or if it isn't valid.
     */
    Path resolveSourceDirectory(String directoryName) throws IOException;

    /**
     * Prepares the target directory.
     * <p>
     * If the target directory doesn't yet exist, it will be created.
     * </p>
     * <p>
     * If the target does exist, it must be according to these rules:
     * </p>
     * <ul>
     * <li>It must be a directory</li>
     * <li>It must be writable</li>
     * <li>It must be empty, or it must have a file called "{@value #MAGISTO_EXPORT_MARKER_FILE}".</li>
     * </ul>
     * <p>
     * The path returned is an absolute path.
     * </p>
     *
     * @param directoryName Name of the target directory.
     * @return Existing, valid, real path to the directory.
     * @throws IOException
     */
    Path prepareTargetDirectory(String directoryName) throws IOException;

    /**
     * Ensures that the source and target directories do not overlap
     *
     * @param sourceDirectory Source directory, must be a real path
     * @param targetDirectory Target directory, must be a real path
     * @throws IOException If the directories overlap
     */
    void requireDistinct(Path sourceDirectory, Path targetDirectory) throws IOException;

    /**
     * Writes the {@value #MAGISTO_EXPORT_MARKER_FILE} to the directory.
     *
     * @param directory Directory, must be a real path.
     */
    void writeTouchFile(Path directory) throws IOException;

    /**
     * @param root Directory to find all paths in.
     * @return All paths in a directory, all relative to the directory itself.
     * @throws IOException If an exception occurred while finding all paths.
     */
    SortedSet<Path> findAllPaths(Path root) throws IOException;

    /**
     * Checks whether the source path is newer than the target path.
     * @param source Source path, must be absolute.
     * @param target Target path, must be absolute.
     * @return {@code true} if source is newer, {@code false} otherwise.
     * @throws IOException If an exception occurred while accessing the file system.
     */
    boolean isSourceNewerThanTarget(Path source, Path target) throws IOException;
}
