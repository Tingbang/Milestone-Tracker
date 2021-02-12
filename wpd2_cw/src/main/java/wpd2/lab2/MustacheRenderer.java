package wpd2.lab2;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MustacheRenderer
{
    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(MustacheRenderer.class);
    private static final String TEMPLATE_ROOT = "templates";
    private MustacheFactory mustacheFactory;

    public MustacheRenderer()
    {
        this(TEMPLATE_ROOT);
    }

    public MustacheRenderer(String templateRoot)
    {
        mustacheFactory = new DefaultMustacheFactory(templateRoot);
    }

    public String render(String templateName, Object model)
    {
        LOG.error("in render 1: template root: " + TEMPLATE_ROOT + ", template name: " + templateName);
        Mustache mustache = mustacheFactory.compile(templateName);
        LOG.error("in render 2: template root: " + TEMPLATE_ROOT + ", template name: " + templateName);

        try (StringWriter stringWriter = new StringWriter())
        {
            mustache.execute(stringWriter, model).close();
            return stringWriter.toString();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

}
