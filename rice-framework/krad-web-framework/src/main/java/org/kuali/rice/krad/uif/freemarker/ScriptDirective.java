package org.kuali.rice.krad.uif.freemarker;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.kuali.rice.krad.uif.component.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;

public class ScriptDirective implements TemplateDirectiveModel {

    @Override
    public void execute(Environment env, Map params, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        String script = FreeMarkerInlineRenderUtils.resolve(env, "value", String.class);

        if (!StringUtils.hasText(script)) {
            return;
        }

        Component component = FreeMarkerInlineRenderUtils.resolve(env, "component", Component.class);
        String role = FreeMarkerInlineRenderUtils.resolve(env, "role", String.class);
        FreeMarkerInlineRenderUtils.renderScript(script, component, role, env.getOut());
    }

}
