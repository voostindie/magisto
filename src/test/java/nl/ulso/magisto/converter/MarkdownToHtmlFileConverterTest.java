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

import org.junit.Test;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.*;

public class MarkdownToHtmlFileConverterTest {

    private final FileConverter fileConverter = new MarkdownToHtmlFileConverter();

    @Test
    public void testMarkdownExtensionMd() throws Exception {
        assertTrue(fileConverter.supports(createPath("foo.md")));
    }

    @Test
    public void testMarkdownExtensionMdown() throws Exception {
        assertTrue(fileConverter.supports(createPath("foo.mdown")));
    }

    @Test
    public void testMarkdownExtensionMarkdown() throws Exception {
        assertTrue(fileConverter.supports(createPath("foo.markdown")));
    }

    @Test
    public void testMarkdownExtensionMarkdownWeirdCasing() throws Exception {
        assertTrue(fileConverter.supports(createPath("foo.MarkDown")));
    }

    @Test
    public void testNormalFile() throws Exception {
        assertFalse(fileConverter.supports(createPath("foo.jpg")));
    }

    @Test
    public void testConvertedFileName() throws Exception {
        assertEquals(createPath("foo.MarkDown.html"), fileConverter.getConvertedFileName(createPath("foo.MarkDown")));
    }
}