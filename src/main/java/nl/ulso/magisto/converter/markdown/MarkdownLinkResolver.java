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

package nl.ulso.magisto.converter.markdown;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for resolving local links to files that may be Markdown files.
 */
final class MarkdownLinkResolver {

    static final Set<String> SOURCE_EXTENSIONS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("md", "markdown", "mdown")));

    static final String TARGET_EXTENSION = "html";

    private MarkdownLinkResolver() {
    }

    static String resolveLink(String originalLink) {
        if (originalLink.contains("://")) {
            return originalLink;
        }
        for (String extension : SOURCE_EXTENSIONS) {
            if (originalLink.toLowerCase().endsWith(extension)) {
                return originalLink.substring(0, originalLink.length() - extension.length()) + TARGET_EXTENSION;
            }
        }
        return originalLink;
    }


}
