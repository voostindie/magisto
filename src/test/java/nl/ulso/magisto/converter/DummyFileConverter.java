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

public class DummyFileConverter implements FileConverter {

    private String loggedConversions = "";

    @Override
    public boolean supports(Path path) {
        return path.getFileName().toString().endsWith(".convert");
    }

    @Override
    public Path getConvertedFileName(Path path) {
        return path.resolveSibling(path.getFileName().toString() + ".converted");
    }

    @Override
    public void convert(FileSystemAccessor fileSystemAccessor, Path sourceRoot, Path targetRoot, Path path) throws IOException {
        loggedConversions += String.format("%s:%s -> %s:%s", sourceRoot.getFileName(), path.getFileName(),
                targetRoot.getFileName(), getConvertedFileName(path).getFileName());
    }

    public String getLoggedConversions() {
        return loggedConversions;
    }

    public void clearRecordings() {
        loggedConversions = "";
    }
}
