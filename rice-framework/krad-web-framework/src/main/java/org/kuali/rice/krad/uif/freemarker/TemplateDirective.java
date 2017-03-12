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

public class TemplateDirective implements TemplateDirectiveModel {
    
    @Override
    public void execute(Environment env, Map params, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        Component component = FreeMarkerInlineRenderUtils.resolve(env, "component", Component.class);

        if (component == null) {
            return;
        }

        String body = FreeMarkerInlineRenderUtils.resolve(env, "body", String.class);
        boolean componentUpdate = Boolean.TRUE.equals(FreeMarkerInlineRenderUtils.resolve(env, "componentUpdate",
                Boolean.class));
        boolean includeSrc = Boolean.TRUE.equals(FreeMarkerInlineRenderUtils.resolve(env, "includeSrc", Boolean.class));
        @SuppressWarnings("unchecked")
        Map<String, TemplateModel> tmplParms = FreeMarkerInlineRenderUtils.resolve(env, "tmplParms", Map.class);
        FreeMarkerInlineRenderUtils.renderTemplate(env, component, body, componentUpdate, includeSrc, tmplParms);

    }

}
