package org.kuali.rice.krad.uif.freemarker;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.kuali.rice.krad.uif.container.CollectionGroup;

import java.io.IOException;
import java.util.Map;

public class CollectionGroupDirective implements TemplateDirectiveModel {

    @Override
    public void execute(Environment env, Map params, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        CollectionGroup group = FreeMarkerInlineRenderUtils.resolve(env, "group", CollectionGroup.class);
        FreeMarkerInlineRenderUtils.renderCollectionGroup(env, group);
    }

}
