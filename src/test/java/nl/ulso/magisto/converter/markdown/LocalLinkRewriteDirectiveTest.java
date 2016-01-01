package nl.ulso.magisto.converter.markdown;

import org.junit.Before;
import org.junit.Test;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;

public class LocalLinkRewriteDirectiveTest {

    private LocalLinkRewriteDirective directive;

    @Before
    public void setUp() throws Exception {
        directive = new LocalLinkRewriteDirective();
    }

    @Test
    public void testLinkRewriteWithFileInRoot() throws Exception {
        final String link = directive.rewriteLink(createPath("test.md"), "/favicon.ico");
        assertEquals("./favicon.ico", link);
    }

    @Test
    public void testLinkRewriteWithFileInRootNoForwardSlash() throws Exception {
        final String link = directive.rewriteLink(createPath("test.md"), "favicon.ico");
        assertEquals("./favicon.ico", link);
    }

    @Test
    public void testLinkRewriteWithFileInSubdirectory1() throws Exception {
        final String link = directive.rewriteLink(createPath("dir", "test.md"), "/favicon.ico");
        assertEquals("../favicon.ico", link);
    }

    @Test
    public void testLinkRewriteWithFileInSubdirectory2() throws Exception {
        final String link = directive.rewriteLink(createPath("dir1", "dir2", "test.md"), "/favicon.ico");
        assertEquals("../../favicon.ico", link);
    }
}