package nl.ulso.magisto.converter.markdown;

import org.parboiled.common.StringUtils;
import org.pegdown.LinkRenderer;
import org.pegdown.ast.ExpLinkNode;
import org.pegdown.ast.RefLinkNode;

import static org.pegdown.FastEncoder.encode;

/**
 * Link renderer that converts links to Markdown files to links to their equivalent HTML files.
 */
class MarkdownLinkRenderer extends LinkRenderer {

    @Override
    public Rendering render(ExpLinkNode node, String text) {
        Rendering rendering = new Rendering(MarkdownLinkResolver.resolveLink(node.url), text);
        return StringUtils.isEmpty(node.title) ? rendering : rendering.withAttribute("title", encode(node.title));
    }

    @Override
    public Rendering render(RefLinkNode node, String url, String title, String text) {
        Rendering rendering = new Rendering(MarkdownLinkResolver.resolveLink(url), text);
        return StringUtils.isEmpty(title) ? rendering : rendering.withAttribute("title", encode(title));
    }
}
