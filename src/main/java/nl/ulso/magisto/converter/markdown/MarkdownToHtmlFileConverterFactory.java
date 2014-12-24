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

import nl.ulso.magisto.converter.FileConverter;
import nl.ulso.magisto.converter.FileConverterFactory;
import nl.ulso.magisto.git.GitClient;
import nl.ulso.magisto.io.FileSystemAccessor;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Default implementation of the {@link nl.ulso.magisto.converter.FileConverterFactory}.
 */
public class MarkdownToHtmlFileConverterFactory implements FileConverterFactory {
    private final GitClient gitClient;

    public MarkdownToHtmlFileConverterFactory(GitClient gitClient) {
        this.gitClient = gitClient;
    }

    @Override
    public FileConverter create(FileSystemAccessor fileSystemAccessor, Path sourceRoot) throws IOException {
        return new MarkdownToHtmlFileConverter(fileSystemAccessor, sourceRoot, gitClient);
    }
}
