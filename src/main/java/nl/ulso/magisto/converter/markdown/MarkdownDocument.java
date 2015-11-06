/*
 * Copyright 2014 Vincent Oostindie
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

package nl.ulso.magisto.converter.markdown;

import org.pegdown.Extensions;
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
            return new PegDownProcessor(Extensions.ALL - Extensions.HARDWRAPS - Extensions.EXTANCHORLINKS);
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
