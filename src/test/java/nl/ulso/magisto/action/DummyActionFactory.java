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

import java.nio.file.Path;

import static nl.ulso.magisto.action.DummyAction.*;

public class DummyActionFactory implements ActionFactory {
    @Override
    public Action copy(Path path) {
        return COPY_ACTION;
    }

    @Override
    public Action convert(Path path) {
        return CONVERT_ACTION;
    }

    @Override
    public Action delete(Path path) {
        return DELETE_ACTION;
    }

    public void clearCounts() {
        COPY_ACTION.clear();
        CONVERT_ACTION.clear();
        DELETE_ACTION.clear();
    }
}
