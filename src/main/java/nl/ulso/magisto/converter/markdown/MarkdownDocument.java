package nl.ulso.magisto.converter.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

/**
 * Represents a Markdown document.
 *
 * TODO: fix the URLs:
 * https://github.com/vsch/flexmark-java/blob/master/flexmark-java-samples/src/com/vladsch/flexmark/java/samples/PegdownCustomLinkResolverOptions.java
 */
public class MarkdownDocument {

    private static final ThreadLocal<Parser> PARSER = new ThreadLocal<Parser>() {
        @Override
        protected Parser initialValue() {
            return Parser.builder().build();
        }
    };

    private static final ThreadLocal<HtmlRenderer> RENDERER = new ThreadLocal<HtmlRenderer>() {
        @Override
        protected HtmlRenderer initialValue() {
            return HtmlRenderer.builder().build();
        }
    };

    private final Node rootNode;

    public MarkdownDocument(String markdownText) {
        rootNode = PARSER.get().parse(markdownText);
    }

    public String extractTitle() {
        return new TitleFinder().extractTitle(rootNode);
    }

    public String toHtml() {
        return RENDERER.get().render(rootNode).trim();
//        return new ToHtmlSerializer(new MarkdownLinkRenderer()).toHtml(rootNode);
    }

}
