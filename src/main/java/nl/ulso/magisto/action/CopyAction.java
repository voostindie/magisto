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

package nl.ulso.magisto.action;

import nl.ulso.magisto.io.FileSystemAccessor;

import java.io.IOException;
import java.nio.file.Path;

import static nl.ulso.magisto.action.ActionType.COPY;
import static nl.ulso.magisto.io.Paths.requireAbsolutePath;

/**
 * Copies a file from the source root to the target root.
 */
class CopyAction extends AbstractAction {

    CopyAction(Path path) {
        super(path);
    }

    @Override
    public ActionType getActionType() {
        return COPY;
    }

    @Override
    public void perform(FileSystemAccessor fileSystemAccessor, Path sourceRoot, Path targetRoot) throws IOException {
        fileSystemAccessor.copy(
                requireAbsolutePath(sourceRoot),
                requireAbsolutePath(targetRoot),
                getPath()
        );
    }
}
