package nl.ulso.magisto.converter.markdown;

import org.parboiled.Parboiled;
import org.pegdown.CustomMarkdownParser;
import org.pegdown.PegDownProcessor;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.ast.RootNode;

/**
 * Represents a Markdown document.
 */
public class MarkdownDocument {

    private static final ThreadLocal<PegDownProcessor> PROCESSOR = new ThreadLocal<PegDownProcessor>() {
        @Override
        protected PegDownProcessor initialValue() {
            final CustomMarkdownParser parser = Parboiled.createParser(CustomMarkdownParser.class);
            return new PegDownProcessor(parser);
        }
    };

    private final RootNode rootNode;

    public MarkdownDocument(char[] markdownText) {
        rootNode = PROCESSOR.get().parseMarkdown(markdownText);
    }

    public String extractTitle() {
        return new TitleFinder().extractTitle(rootNode);
    }

    public String toHtml() {
        return new ToHtmlSerializer(new MarkdownLinkRenderer()).toHtml(rootNode);
    }

}
