package nl.ulso.magisto.converter.markdown;

import org.pegdown.ast.*;

/**
 * Finds the first level 1 title in a Markdown document.
 */
class TitleFinder extends AbstractVisitor {

    private final StringBuilder buffer;
    private boolean isFound;
    private boolean isBuffering;

    public TitleFinder() {
        buffer = new StringBuilder();
        isFound = false;
        isBuffering = false;
    }

    @Override
    public void visit(RootNode node) {
        visitChildren(node);
    }

    private void visitChildren(SuperNode node) {
        for (Node child : node.getChildren()) {
            if (isFound) {
                return;
            }
            child.accept(this);
        }
    }

    @Override
    public void visit(HeaderNode node) {
        if (node.getLevel() == 1) {
            isBuffering = true;
            visitChildren(node);
            isBuffering = false;
            isFound = true;
        }
    }

    @Override
    public void visit(AnchorLinkNode node) {
        if (isBuffering) {
            buffer.append(node.getText());
        }
    }

    @Override
    public void visit(TextNode node) {
        if (isBuffering) {
            buffer.append(node.getText());
        }
    }

    public String extractTitle(RootNode rootNode) {
        rootNode.accept(this);
        return buffer.toString();
    }
}
