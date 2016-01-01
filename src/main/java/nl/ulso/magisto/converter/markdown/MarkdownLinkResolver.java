package nl.ulso.magisto.converter.markdown;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for resolving local links to files that may be Markdown files.
 */
final class MarkdownLinkResolver {

    static final Set<String> SOURCE_EXTENSIONS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("md", "markdown", "mdown")));

    static final String TARGET_EXTENSION = "html";

    private MarkdownLinkResolver() {
    }

    static String resolveLink(String originalLink) {
        if (originalLink.contains("://")) {
            return originalLink;
        }
        for (String extension : SOURCE_EXTENSIONS) {
            if (originalLink.toLowerCase().endsWith(extension)) {
                return originalLink.substring(0, originalLink.length() - extension.length()) + TARGET_EXTENSION;
            }
        }
        return originalLink;
    }


}
