package kh.edu.istad.platform.iamweb.view;

import gg.jte.TemplateEngine;
import gg.jte.output.Utf8ByteOutput;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.View;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Bridges a JTE template to Spring MVC's view contract.
 */
public class JteView implements View {

    private final String templateName;
    private final TemplateEngine templateEngine;

    public JteView(String templateName, TemplateEngine templateEngine) {
        this.templateName = templateName;
        this.templateEngine = templateEngine;
    }

    @Override
    public String getContentType() {
        return "text/html;charset=UTF-8";
    }

    @Override
    public void render(
            Map<String, ?> model,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        // A plain copy is required: the model legitimately contains null values.
        Map<String, Object> params = new LinkedHashMap<>();
        model.forEach(params::put);

        Utf8ByteOutput output = new Utf8ByteOutput();
        templateEngine.render(templateName, params, output);

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        output.writeTo(response.getOutputStream());
    }
}
