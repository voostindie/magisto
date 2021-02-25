package nl.ulso.magisto.converter.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.LinkResolver;
import com.vladsch.flexmark.html.LinkResolverFactory;
import com.vladsch.flexmark.html.renderer.LinkResolverBasicContext;
import com.vladsch.flexmark.html.renderer.ResolvedLink;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.misc.Extension;

import java.util.Set;

final class MarkdownLinkRenderer implements HtmlRenderer.HtmlRendererExtension {

    public static Extension create() {
        return new MarkdownLinkRenderer();
    }

    @Override
    public void rendererOptions(MutableDataHolder mutableDataHolder) {
    }

    @Override
    public void extend(HtmlRenderer.Builder htmlRenderer, String rendererType) {
        htmlRenderer.linkResolverFactory(new CustomLinkResolver.Factory());
    }

    private static final class CustomLinkResolver implements LinkResolver {
        @Override
        public ResolvedLink resolveLink(Node node, LinkResolverBasicContext context, ResolvedLink link) {
            return link.withUrl(MarkdownLinkResolver.resolveLink(link.getUrl()));
        }

        private static class Factory implements LinkResolverFactory {
            @Override
            public Set<Class<?>> getAfterDependents() {
                return null;
            }

            @Override
            public Set<Class<?>> getBeforeDependents() {
                return null;
            }

            @Override
            public boolean affectsGlobalScope() {
                return false;
            }

            @Override
            public LinkResolver apply(LinkResolverBasicContext linkResolverBasicContext) {
                return new CustomLinkResolver();
            }
        }
    }
}
