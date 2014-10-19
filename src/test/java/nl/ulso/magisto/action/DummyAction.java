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

import java.nio.file.Path;

public class DummyAction implements Action {

    public static final DummyAction COPY_ACTION = new DummyAction(ActionType.COPY);
    public static final DummyAction DELETE_ACTION = new DummyAction(ActionType.DELETE);
    public static final DummyAction CONVERT_ACTION = new DummyAction(ActionType.CONVERT);

    private final ActionType type;
    private int count;

    public DummyAction(ActionType type) {
        this.type = type;
        this.count = 0;
    }

    @Override
    public ActionType getActionType() {
        return type;
    }

    public int getCount() {
        return count;
    }

    @Override
    public void perform(FileSystemAccessor fileSystemAccessor, Path sourceRoot, Path targetRoot) {
        count++;
    }

    void clear() {
        count = 0;
    }
}
