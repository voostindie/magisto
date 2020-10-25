package nl.ulso.magisto.converter.markdown;

import freemarker.template.Template;
import nl.ulso.magisto.git.DummyGitClient;
import nl.ulso.magisto.io.DummyFileSystem;
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
    private DummyFileSystem fileSystemAccessor;
    private DummyGitClient gitClient;
    private Path sourcePath;

    @Before
    public void setUp() throws Exception {
        this.fileSystemAccessor = new DummyFileSystem();
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
    public void testConvertedFileNameAtx() throws Exception {
        assertEquals(createPath("foo.html"), fileConverter.getConvertedFileName(createPath("foo.MarkDown")));
    }

    @Test
    public void testCreatePageModel() throws Exception {
        final Date start = new Date();
        TimeUnit.SECONDS.sleep(1);
        final Map<String, Object> model = fileConverter.createPageModel(createPath("test.md"),
                new MarkdownDocument("# Title\n\nParagraph"));
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