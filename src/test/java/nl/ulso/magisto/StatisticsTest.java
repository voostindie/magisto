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

package nl.ulso.magisto;

import org.apache.commons.io.output.WriterOutputStream;
import org.junit.Test;

import java.io.PrintStream;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class StatisticsTest {

    @Test(expected = IllegalStateException.class)
    public void testBeginMayBeCalledOnlyOnce() throws Exception {
        new Statistics().begin().begin();
    }

    @Test(expected = IllegalStateException.class)
    public void testEndCannotBeCalledWithoutBegin() throws Exception {
        new Statistics().end();
    }

    @Test(expected = IllegalStateException.class)
    public void testPrintCannotBeCalledWithoutEnd() throws Exception {
        new Statistics().begin().print(System.out);
    }

    @Test
    public void testSuccessfulRun() throws Exception {
        final StringWriter writer = new StringWriter();
        final PrintStream stream = new PrintStream(new WriterOutputStream(writer));
        new Statistics().begin().end().print(stream);
        stream.flush();
        stream.close();
        assertThat(writer.toString(), containsString("Done!"));
    }
}