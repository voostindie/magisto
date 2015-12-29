/*
 * Copyright 2015 Vincent Oostindie
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

package org.pegdown;

import org.pegdown.ast.Node;
import org.pegdown.ast.SuperNode;
import org.pegdown.ast.TextNode;
import org.pegdown.plugins.PegDownPlugins;

/**
 * Custom Markdown parser that ensures that code in headers is added to the generated anchor names.
 * <p>
 * This class has to be in package {@code org.pegdown} because it references package-scoped code.
 * </p>
 * <p>
 * Basically this class copies the {@link #collectChildrensText(SuperNode, AnchorNodeInfo)} from the parent class and
 * changes one line it, from:
 * </p>
 * <pre><code>
 * if (child.getClass() == TextNode.class || child.getClass() == SpecialTextNode.class) {
 * </code></pre>
 * <p>
 * to:
 * </p>
 * <pre><code>
 * if (child instanceof TextNode) {
 * </code></pre>
 * ...and that seems to do the trick.
 */
public class CustomMarkdownParser extends Parser {

    public CustomMarkdownParser() {
        super(Extensions.ALL - Extensions.HARDWRAPS - Extensions.EXTANCHORLINKS,
                PegDownProcessor.DEFAULT_MAX_PARSING_TIME,
                Parser.DefaultParseRunnerProvider,
                PegDownPlugins.NONE);
    }

    @Override
    public void collectChildrensText(SuperNode node, AnchorNodeInfo nodeInfo) {
        for (Node child : node.getChildren()) {
            // accumulate all the text
            if (child instanceof TextNode) {
                nodeInfo.text.append(((TextNode) child).getText());
                if (nodeInfo.startIndex == 0) {
                    nodeInfo.startIndex = child.getStartIndex();
                }
                nodeInfo.endIndex = child.getEndIndex();
            } else if (child instanceof SuperNode) {
                collectChildrensText((SuperNode) child, nodeInfo);
            }
        }
    }
}
