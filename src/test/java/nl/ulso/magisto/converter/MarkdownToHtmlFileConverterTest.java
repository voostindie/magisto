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

import nl.ulso.magisto.io.DummyFileSystemAccessor;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.*;

public class MarkdownToHtmlFileConverterTest {

    private final MarkdownToHtmlFileConverter fileConverter = new MarkdownToHtmlFileConverter();

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
        assertEquals(createPath("foo.html"), fileConverter.getConvertedFileName(createPath("foo.MarkDown")));
    }

    @Test
    public void testTitleExtraction() throws Exception {
        assertEquals("title", fileConverter.extractTitleFromMarkdown("abstract\n\n# title\n\n## subtitle\n\nsome text"));
    }

    @Test
    public void testCreatePageModel() throws Exception {
        final Date start = new Date();
        TimeUnit.SECONDS.sleep(1);
        final Map<String, Object> model = fileConverter.createPageModel(createPath("test.md"),
                String.format("# Title%n%nParagraph"));
        TimeUnit.SECONDS.sleep(1);
        final Date end = new Date();
        final Date timestamp = (Date) model.get("timestamp");
        assertTrue(timestamp.after(start));
        assertTrue(timestamp.before(end));
        assertEquals("test.md", ((Path) model.get("path")).getFileName().toString());
        assertEquals("Title", model.get("title"));
        assertNotNull(model.get("content"));
    }

    @Test
    public void testConvertMarkdownFile() throws Exception {
        final DummyFileSystemAccessor fileSystemAccessor = new DummyFileSystemAccessor();
        fileSystemAccessor.registerTextFileForBufferedReader("test.md", String.format("# Title%n%nParagraph"));
        fileConverter.convert(fileSystemAccessor, createPath("."), createPath("."), createPath("test.md"));
        final String output = fileSystemAccessor.getTextFileFromBufferedWriter("test.html");
        assertNotNull(output);
        System.out.println("output = " + output);
    }

    @Test
    public void testNormalFileLink() throws Exception {
        final String html = fileConverter.convertMarkdownToHtml("[link](image.jpg)");
        assertEquals("<p><a href=\"image.jpg\">link</a></p>", html);
    }

    @Test
    public void testExternalLink() throws Exception {
        final String html = fileConverter.convertMarkdownToHtml("[link](http://www.github.com/voostindie/magisto)");
        assertEquals("<p><a href=\"http://www.github.com/voostindie/magisto\">link</a></p>", html);
    }

    @Test
    public void testMarkdownFileLink() throws Exception {
        final String html = fileConverter.convertMarkdownToHtml("[link](file.md)");
        assertEquals("<p><a href=\"file.html\">link</a></p>", html);
    }

    @Test
    public void testExternalMarkdownLink() throws Exception {
        final String html = fileConverter.convertMarkdownToHtml("[link](http://www.example.com/file.md)");
        assertEquals("<p><a href=\"http://www.example.com/file.md\">link</a></p>", html);
    }

    @Test
    public void testMarkdownFileReferenceLink() throws Exception {
        final String html = fileConverter.convertMarkdownToHtml(String.format("[link][id]%n%n[id]: file.md"));
        assertEquals("<p><a href=\"file.html\">link</a></p>", html);
    }
}