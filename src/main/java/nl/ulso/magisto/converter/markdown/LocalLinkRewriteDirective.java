package nl.ulso.magisto.converter.markdown;

import freemarker.core.Environment;
import freemarker.ext.beans.StringModel;
import freemarker.template.*;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Map;

/**
 * FreeMarker directive that rewrites local links of the form {@code /path/to/file} to {@code ../../path/to/file} in
 * the template; inserting as many parent directory navigation steps as needed. This allows templates to include static
 * files that are always resolved correctly, no matter at what level the template is processed.
 */
public class LocalLinkRewriteDirective implements TemplateDirectiveModel {

    private static final String MODEL_PARAMETER_NAME = "path";
    private static final String DIRECTIVE_PARAMETER_NAME = "path";

    private static final String SUPER = "..";
    private static final String SLASH = "/";

    @Override
    public void execute(Environment environment, Map parameters, TemplateModel[] loopVariables,
                        TemplateDirectiveBody body) throws TemplateException, IOException {
        if (parameters.size() != 1) {
            throw new TemplateException("Directive requires a link as a single parameter", environment);
        }
        final Object parameter = parameters.get(DIRECTIVE_PARAMETER_NAME);
        if (parameter == null) {
            throw new TemplateException("Missing parameter " + DIRECTIVE_PARAMETER_NAME, environment);
        }
        if (!(parameter instanceof SimpleScalar)) {
            throw new TemplateException("Expected a SimpleScalar. Got a(n) " + parameter.getClass().getSimpleName(),
                    environment);
        }
        final String link = ((SimpleScalar) parameter).getAsString();
        final Path filePath = (Path) ((StringModel) environment.getDataModel().get(MODEL_PARAMETER_NAME)).getWrappedObject();
        final Writer writer = environment.getOut();
        writer.write(rewriteLink(filePath, link));
    }

    String rewriteLink(Path filePath, String link) {
        final StringBuilder rewrite = new StringBuilder();
        final int pathParts = filePath.getNameCount() - 1;
        if (pathParts == 0) {
            rewrite.append(".");
        } else {
            for (int i = 0; i < pathParts; i++) {
                if (i > 0) {
                    rewrite.append(SLASH);
                }
                rewrite.append(SUPER);
            }
        }
        if (!link.startsWith(SLASH)) {
            rewrite.append(SLASH);
        }
        rewrite.append(link);
        return rewrite.toString();
    }
}
