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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DummyFileConverter implements FileConverter {

    private final boolean isCustomTemplateChanged;
    private String loggedConversions = "";

    public DummyFileConverter() {
        this(false);
    }

    public DummyFileConverter(boolean isCustomTemplateChanged) {
        this.isCustomTemplateChanged = isCustomTemplateChanged;
    }

    @Override
    public Set<String> getSourceExtensions() {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList("convert")));
    }

    @Override
    public String getTargetExtension() {
        return "converted";
    }

    @Override
    public boolean supports(Path path) {
        return path.getFileName().toString().endsWith(".convert");
    }

    @Override
    public Path getConvertedFileName(Path path) {
        return path.resolveSibling(path.getFileName().toString() + "ed");
    }

    @Override
    public void convert(FileSystemAccessor fileSystemAccessor, Path sourceRoot, Path targetRoot, Path path)
            throws IOException {
        loggedConversions += String.format("%s:%s -> %s:%s", sourceRoot.getFileName(), path.getFileName(),
                targetRoot.getFileName(), getConvertedFileName(path).getFileName());
    }

    @Override
    public boolean isCustomTemplateChanged(FileSystemAccessor fileSystemAccessor, Path sourceRoot, Path targetRoot)
            throws IOException {
        return isCustomTemplateChanged;
    }

    public String getLoggedConversions() {
        return loggedConversions;
    }

    public void clearRecordings() {
        loggedConversions = "";
    }
}
