package org.kuali.rice.krad.uif.freemarker;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.layout.StackedLayoutManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class StackedDirective implements TemplateDirectiveModel {

    @Override
    public void execute(Environment env, Map params, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        @SuppressWarnings("unchecked")
        List<? extends Component> items = FreeMarkerInlineRenderUtils.resolve(env, "items", List.class);
        StackedLayoutManager manager = FreeMarkerInlineRenderUtils.resolve(env, "manager", StackedLayoutManager.class);
        CollectionGroup container = FreeMarkerInlineRenderUtils.resolve(env, "container", CollectionGroup.class);
        FreeMarkerInlineRenderUtils.renderStacked(env, items, manager, container);
    }

}
