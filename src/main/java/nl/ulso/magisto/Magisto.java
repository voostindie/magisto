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

import nl.ulso.magisto.io.FileSystemAccessor;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Knits all the components in the Magisto system together (like a module) and runs it.
 */
public class Magisto {

    private final FileSystemAccessor fileSystemAccessor;

    public Magisto(FileSystemAccessor fileSystemAccessor) {
        this.fileSystemAccessor = fileSystemAccessor;
    }

    public Statistics run(final String sourceDirectory, final String targetDirectory) throws IOException {
        final Statistics statistics = new Statistics();
        try {
            statistics.begin();
            final Path source = fileSystemAccessor.resolveSourceDirectory(sourceDirectory);
            final Path target = fileSystemAccessor.prepareTargetDirectory(targetDirectory);
            fileSystemAccessor.requireDistinct(source, target);

            // TODO: invoke the actual work here!

            fileSystemAccessor.writeTouchFile(target);
        } finally {
            statistics.end();
        }
        return statistics;
    }
}
