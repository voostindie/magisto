package nl.ulso.magisto.converter.markdown;

import com.vladsch.flexmark.ext.anchorlink.AnchorLinkExtension;
import com.vladsch.flexmark.ext.typographic.TypographicExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataSet;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.util.List;

/**
 * Represents a Markdown document.
 */
public class MarkdownDocument {

    private static final DataSet OPTIONS = createOptions();

    private static final ThreadLocal<Parser> PARSER = new ThreadLocal<Parser>() {
        @Override
        protected Parser initialValue() {
            return Parser.builder(OPTIONS).build();
        }
    };

    private static final ThreadLocal<HtmlRenderer> RENDERER = new ThreadLocal<HtmlRenderer>() {
        @Override
        protected HtmlRenderer initialValue() {
            return HtmlRenderer.builder(OPTIONS).build();
        }
    };

    private static DataSet createOptions() {
        return new MutableDataSet()
                .set(Parser.EXTENSIONS, List.of(
                        AnchorLinkExtension.create(),
                        TypographicExtension.create(),
                        MarkdownLinkRenderer.create()))
                .set(AnchorLinkExtension.ANCHORLINKS_SET_NAME, true)
                .set(AnchorLinkExtension.ANCHORLINKS_SET_ID, false);
    }

    private final Node rootNode;

    public MarkdownDocument(String markdownText) {
        rootNode = PARSER.get().parse(markdownText);
    }

    public String extractTitle() {
        return new TitleFinder().extractTitle(rootNode);
    }

    public String toHtml() {
        return RENDERER.get().render(rootNode).trim();
    }
}
