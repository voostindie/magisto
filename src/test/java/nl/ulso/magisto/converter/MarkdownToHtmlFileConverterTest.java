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

import freemarker.template.Template;
import nl.ulso.magisto.git.DummyGitClient;
import nl.ulso.magisto.io.DummyFileSystemAccessor;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static nl.ulso.magisto.io.DummyPathEntry.createPathEntry;
import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.*;

public class MarkdownToHtmlFileConverterTest {

    private MarkdownToHtmlFileConverter fileConverter;
    private DummyFileSystemAccessor fileSystemAccessor;
    private DummyGitClient gitClient;
    private Path sourcePath;

    @Before
    public void setUp() throws Exception {
        this.fileSystemAccessor = new DummyFileSystemAccessor();
        this.sourcePath = fileSystemAccessor.resolveSourceDirectory("source");
        fileSystemAccessor.prepareTargetDirectory("target");
        this.gitClient = new DummyGitClient();
        this.fileConverter = new MarkdownToHtmlFileConverter(fileSystemAccessor, createPath("."), gitClient);
    }

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
        assertNotNull(model.get("history"));
    }

    @Test
    public void testConvertMarkdownFile() throws Exception {
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

    @Test
    public void testLoadDefaultTemplate() throws Exception {
        Template template = fileConverter.loadDefaultTemplate();
        assertNotNull(template);
        assertEquals("page_template.ftl", template.getName());
    }

    @Test
    public void testLoadCustomTemplate() throws Exception {
        fileSystemAccessor.addSourcePaths(createPathEntry(".page.ftl"));
        fileSystemAccessor.registerTextFileForBufferedReader(".page.ftl", "CUSTOM TEMPLATE");
        final Template template = fileConverter.loadCustomTemplate(fileSystemAccessor, sourcePath);
        assertNotNull(template);
        assertEquals(".page.ftl", template.getName());
    }

    @Test
    public void testConvertLocalLinkInTemplate() throws Exception {
        fileSystemAccessor.addSourcePaths(createPathEntry(".page.ftl"));
        fileSystemAccessor.registerTextFileForBufferedReader(".page.ftl", "<@link path=\"/static/favicon.ico\"/>");
        fileSystemAccessor.registerTextFileForBufferedReader("test.md", String.format("# Title%n%nParagraph"));
        this.fileConverter = new MarkdownToHtmlFileConverter(fileSystemAccessor, createPath("."), gitClient);
        fileConverter.convert(fileSystemAccessor, createPath("."), createPath("."), createPath("dir", "test.md"));
        final String output = fileSystemAccessor.getTextFileFromBufferedWriter("test.html");
        assertEquals("../static/favicon.ico", output);
    }

    @Test
    public void testCustomTemplateHasNotChanged() throws Exception {
        fileSystemAccessor.addSourcePaths(createPathEntry(".page.ftl"));
        assertFalse(fileConverter.isCustomTemplateChanged(fileSystemAccessor, createPath("."), createPath(".")));
    }

    @Test
    public void testCustomTemplateHasChanged() throws Exception {
        fileSystemAccessor.markTouchFile();
        final Path sourceRoot = fileSystemAccessor.resolveSourceDirectory(".");
        fileSystemAccessor.addSourcePaths(createPathEntry(sourceRoot.resolve(".page.ftl")));
        fileSystemAccessor.registerTextFileForBufferedReader(".page.ftl", "CUSTOM TEMPLATE");
        assertTrue(fileConverter.isCustomTemplateChanged(fileSystemAccessor, sourceRoot, createPath(".")));
    }
}