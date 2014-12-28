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

import nl.ulso.magisto.io.Paths;

import java.nio.file.Path;
import java.util.Comparator;

/**
 * Abstract base class for path comparisons that initially compares paths without their extension. If these paths are
 * equal, a comparison on extension is performed (to be implemented by subclasses).
 */
abstract class AbstractPathComparator implements Comparator<Path> {

    @Override
    public final int compare(Path first, Path second) {
        final Paths.ExtensionLessPath firstPath = Paths.splitOnExtension(first);
        final Paths.ExtensionLessPath secondPath = Paths.splitOnExtension(second);

        final int pathComparison = firstPath.getPathWithoutExtension().compareTo(secondPath.getPathWithoutExtension());
        if (pathComparison != 0) {
            return pathComparison;
        }

        return compareExtensions(firstPath.getOriginalExtension(), secondPath.getOriginalExtension());
    }

    protected abstract int compareExtensions(String firstExtension, String secondExtension);
}
