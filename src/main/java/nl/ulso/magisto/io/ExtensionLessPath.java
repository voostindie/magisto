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

import java.nio.file.Path;

/**
 * Represents a path with the extension removed and available separately.
 */
public class ExtensionLessPath {

    private final Path pathWithoutExtension;
    private final String originalExtension;

    ExtensionLessPath(Path path) {
        final String filename = path.getName(path.getNameCount() - 1).toString();
        final int position = filename.lastIndexOf('.');
        if (position < 1) {
            pathWithoutExtension = path;
            originalExtension = "";
        } else {
            pathWithoutExtension = path.resolveSibling(filename.substring(0, position));
            originalExtension = filename.substring(position + 1);
        }
    }

    public Path getPathWithoutExtension() {
        return pathWithoutExtension;
    }

    public String getOriginalExtension() {
        return originalExtension;
    }
}
