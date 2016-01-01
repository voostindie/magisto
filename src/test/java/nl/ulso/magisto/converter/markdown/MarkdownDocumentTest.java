package nl.ulso.magisto.converter.markdown;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MarkdownDocumentTest {

    private MarkdownDocument createMarkdownDocument(String text) {
        return new MarkdownDocument(text.toCharArray());
    }

    @Test
    public void testTitleExtractionAtxHeader() throws Exception {
        assertEquals("Title", createMarkdownDocument(
                "abstract\n\n# Title\n\n## subtitle\n\nsome text").extractTitle());
    }

    @Test
    public void testTitleExtractionSetextHeader() throws Exception {
        assertEquals("Title", createMarkdownDocument(
                "abstract\n\nTitle\n=====\n\nsubtitle\n--------\n\nsome text").extractTitle());
    }

    @Test
    public void testTitleExtractionNoTitlePresent() throws Exception {
        assertEquals("", createMarkdownDocument("some text").extractTitle());
    }

    @Test
    public void testAnchorInHeader() throws Exception {
        final String html = createMarkdownDocument("# A long \"title\"'s great!").toHtml();
        assertEquals("<h1><a href=\"#a-long-titles-great-\" name=\"a-long-titles-great-\"></a>A long &ldquo;title&rdquo;&rsquo;s great!</h1>", html);
    }

    @Test
    public void testAnchorInHeaderWithCode() throws Exception {
        final String html = createMarkdownDocument("# Title with `code`").toHtml();
        assertEquals("<h1><a href=\"#title-with-code\" name=\"title-with-code\"></a>Title with <code>code</code></h1>", html);
    }

    @Test
    public void testNormalFileLink() throws Exception {
        final String html = createMarkdownDocument("[link](image.jpg)").toHtml();
        assertEquals("<p><a href=\"image.jpg\">link</a></p>", html);
    }

    @Test
    public void testExternalLink() throws Exception {
        final String html = createMarkdownDocument("[link](http://www.github.com/voostindie/magisto)").toHtml();
        assertEquals("<p><a href=\"http://www.github.com/voostindie/magisto\">link</a></p>", html);
    }

    @Test
    public void testMarkdownFileLink() throws Exception {
        final String html = createMarkdownDocument("[link](file.md)").toHtml();
        assertEquals("<p><a href=\"file.html\">link</a></p>", html);
    }

    @Test
    public void testExternalMarkdownLink() throws Exception {
        final String html = createMarkdownDocument("[link](http://www.example.com/file.md)").toHtml();
        assertEquals("<p><a href=\"http://www.example.com/file.md\">link</a></p>", html);
    }

    @Test
    public void testMarkdownFileReferenceLink() throws Exception {
        final String html = createMarkdownDocument(String.format("[link][id]%n%n[id]: file.md")).toHtml();
        assertEquals("<p><a href=\"file.html\">link</a></p>", html);
    }


}