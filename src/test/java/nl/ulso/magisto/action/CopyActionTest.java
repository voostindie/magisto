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

import nl.ulso.magisto.io.DummyFileSystemAccessor;
import nl.ulso.magisto.io.DummyPathEntry;
import org.junit.Test;

import java.nio.file.Path;

import static nl.ulso.magisto.io.DummyPathEntry.createPathEntry;
import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;

public class CopyActionTest {

    @Test
    public void testActionType() throws Exception {
        assertEquals(ActionType.COPY, new CopyAction(createPath("copy")).getActionType());
    }

    @Test
    public void testCopy() throws Exception {
        final DummyFileSystemAccessor accessor = new DummyFileSystemAccessor();
        final Path sourceRoot = accessor.resolveSourceDirectory("source");
        final Path targetRoot = accessor.prepareTargetDirectory("target");
        final DummyPathEntry entry = createPathEntry("file");
        new CopyAction(entry.getPath()).perform(accessor, sourceRoot, targetRoot);
        assertEquals("source:file -> target", accessor.getLoggedCopies());
    }
}