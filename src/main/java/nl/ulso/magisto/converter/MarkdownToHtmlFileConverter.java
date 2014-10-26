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

package nl.ulso.magisto.converter;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import nl.ulso.magisto.io.FileSystemAccessor;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.MULTILINE;

/**
 * Converts Markdown files to HTML.
 */
class MarkdownToHtmlFileConverter implements FileConverter {

    private static final String TEMPLATE_PATH = "/nl/ulso/magisto";
    private static final String DEFAULT_PAGE_TEMPLATE = "page_template.ftl";
    private static final String CUSTOM_PAGE_TEMPLATE = ".page.ftl";
    private static final Pattern TITLE_PATTERN = Pattern.compile("^#+ (.*)$", MULTILINE);
    static final Set<String> MARKDOWN_EXTENSIONS = new HashSet<>(Arrays.asList("md", "markdown", "mdown"));

    private final Template template;
    private final PegDownProcessor markdownProcessor;
    private final CustomLinkRenderer linkRenderer;

    MarkdownToHtmlFileConverter(Path sourceRoot) {
        try {
            template = loadTemplate(sourceRoot);
        } catch (IOException e) {
            throw new RuntimeException("Could not load built-in template", e);
        }
        markdownProcessor = new PegDownProcessor(Extensions.ALL - Extensions.HARDWRAPS);
        linkRenderer = new CustomLinkRenderer();
    }

    Template loadTemplate(Path sourceRoot) throws IOException {
        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_21);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setDateTimeFormat("long");
        // TODO: Replace direct file access with the FileSystemAccessor. Probably requires a custom TemplateLoader.
        if (Files.exists(sourceRoot.resolve(CUSTOM_PAGE_TEMPLATE))) {
            configuration.setDirectoryForTemplateLoading(sourceRoot.toFile());
            return configuration.getTemplate(CUSTOM_PAGE_TEMPLATE);
        } else {
            configuration.setClassForTemplateLoading(MarkdownToHtmlFileConverter.class, TEMPLATE_PATH);
            return configuration.getTemplate(DEFAULT_PAGE_TEMPLATE);
        }
    }

    @Override
    public boolean supports(Path path) {
        return MARKDOWN_EXTENSIONS.contains(getFileExtension(path));
    }

    private String getFileExtension(Path path) {
        final String fileName = path.getFileName().toString();
        final int position = fileName.lastIndexOf('.');
        if (position < 1) {
            return "";
        }
        if (position == fileName.length()) {
            return "";
        }
        return fileName.substring(position + 1).toLowerCase();
    }

    @Override
    public Path getConvertedFileName(Path path) {
        return path.resolveSibling(linkRenderer.resolveLink(path.getFileName().toString()));
    }

    @Override
    public void convert(FileSystemAccessor fileSystemAccessor, Path sourceRoot, Path targetRoot, Path path)
            throws IOException {
        Logger.getGlobal().log(Level.INFO, String.format("Converting '%s' from Markdown to HTML.", path));
        final Path targetFile = targetRoot.resolve(getConvertedFileName(path));
        try (final Writer writer = fileSystemAccessor.newBufferedWriterForTextFile(targetFile)) {
            final String markdownText = readTextFile(fileSystemAccessor, sourceRoot.resolve(path));
            final Map<String, Object> model = createPageModel(path, markdownText);
            template.process(model, writer);
        } catch (TemplateException e) {
            throw new RuntimeException("Error in built-in FreeMarker template", e);
        }
    }

    Map<String, Object> createPageModel(Path path, String markdownText) {
        final Map<String, Object> model = new HashMap<>();
        model.put("timestamp", new Date());
        model.put("path", path);
        model.put("title", extractTitleFromMarkdown(markdownText));
        model.put("content", convertMarkdownToHtml(markdownText));
        return model;
    }

    String extractTitleFromMarkdown(String markdownText) {
        final Matcher matcher = TITLE_PATTERN.matcher(markdownText);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    String convertMarkdownToHtml(String markdownText) {
        return markdownProcessor.markdownToHtml(markdownText.toCharArray(), linkRenderer);
    }

    private String readTextFile(FileSystemAccessor fileSystemAccessor, Path path) throws IOException {
        try (final BufferedReader reader = fileSystemAccessor.newBufferedReaderForTextFile(path)) {
            final StringBuilder builder = new StringBuilder();
            while (reader.ready()) {
                final String line = reader.readLine();
                if (line == null) {
                    break;
                }
                builder.append(line);
                builder.append(System.lineSeparator());
            }
            return builder.toString();
        }
    }
}
