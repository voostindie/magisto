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

package nl.ulso.magisto.converter;

import nl.ulso.magisto.io.FileSystemAccessor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Converts Markdown files to HTML.
 */
public class MarkdownToHtmlFileConverter implements FileConverter {

    private final Set<String> MARKDOWN_EXTENSIONS = new HashSet<>(Arrays.asList("md", "markdown", "mdown"));

    @Override
    public boolean supports(Path path) {
        return MARKDOWN_EXTENSIONS.contains(getFileExtension(path));
    }

    private String getFileExtension(Path path) {
        final String fileName = path.getFileName().toString();
        final int position = fileName.lastIndexOf('.');
        if (position < 1) {
            return "";
        }
        if (position == fileName.length()) {
            return "";
        }
        return fileName.substring(position + 1).toLowerCase();
    }

    @Override
    public Path getConvertedFileName(Path path) {
        return path.resolveSibling(path.getFileName().toString() + ".html");
    }

    @Override
    public void convert(FileSystemAccessor fileSystemAccessor, Path sourceRoot, Path targetRoot, Path path)
            throws IOException {
        throw new IllegalStateException("Not implemented yet!");
    }
}
