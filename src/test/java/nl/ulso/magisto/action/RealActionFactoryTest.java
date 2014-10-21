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

import nl.ulso.magisto.converter.DummyFileConverter;
import org.junit.Test;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertNotNull;

public class RealActionFactoryTest {

    private final ActionFactory factory = new RealActionFactory();

    @Test
    public void testSkipAction() throws Exception {
        assertNotNull(factory.skip(createPath("skip")));
    }

    @Test
    public void testCopyAction() throws Exception {
        assertNotNull(factory.copy(createPath("copy")));
    }

    @Test
    public void testDeleteAction() throws Exception {
        assertNotNull(factory.copy(createPath("delete")));
    }

    @Test
    public void testConvertAction() throws Exception {
        assertNotNull(factory.convert(createPath("convert"), new DummyFileConverter()));
    }
}