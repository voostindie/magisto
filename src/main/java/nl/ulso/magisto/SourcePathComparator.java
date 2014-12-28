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

import java.util.Collections;
import java.util.Set;

/**
 * Compares paths, making sure that files that must be converted end up before normal files, based on their extension.
 */
public class SourcePathComparator extends AbstractPathComparator {

    private final Set<String> sourceExtensions;

    public SourcePathComparator(Set<String> sourceExtensions) {
        this.sourceExtensions = Collections.unmodifiableSet(sourceExtensions);
    }

    @Override
    protected int compareExtensions(String firstExtension, String secondExtension) {
        if (sourceExtensions.contains(firstExtension.toLowerCase())) {
            return -1;
        }
        if (sourceExtensions.contains(secondExtension.toLowerCase())) {
            return 1;
        }
        return firstExtension.compareTo(secondExtension);
    }
}
