/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.uif.freemarker;

import java.util.Collections;

import org.apache.log4j.Logger;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;

import freemarker.core.Environment;
import freemarker.core.Macro;
import freemarker.template.TemplateModel;

/**
 * Perform actual rendering on a component during the lifecycle.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RenderComponentTask extends ViewLifecycleTaskBase<Component> {

    private static final Logger LOG = Logger.getLogger(RenderComponentTask.class);

    /**
     * Constructor.
     * 
     * @param phase The render phase for the component.
     */
    public RenderComponentTask() {
        super(Component.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performLifecycleTask() {
        Component component = (Component) getElementState().getElement();
        if (!component.isRender() || component.getTemplate() == null) {
            return;
        }
        
        LifecycleRenderingContext renderingContext = ViewLifecycle.getRenderingContext();
        renderingContext.clearRenderingBuffer();

        renderingContext.importTemplate(component.getTemplate());

        for (String additionalTemplate : component.getAdditionalTemplates()) {
            renderingContext.importTemplate(additionalTemplate);
        }

        try {
            Environment env = renderingContext.getEnvironment();

            // Check for a single-arg macro, with the parameter name "component"
            // defer for parent rendering if not found
            Macro fmMacro = (Macro) env.getMainNamespace().get(component.getTemplateName());

            if (fmMacro == null) {
                return;
            }

            String[] args = fmMacro.getArgumentNames();
            if (args == null || args.length != 1 || !component.getComponentTypeName().equals(args[0])) {
                return;
            }

            FreeMarkerInlineRenderUtils.renderTemplate(env, component,
                    null, false, false, Collections.<String, TemplateModel> emptyMap());

            component.setRenderedHtmlOutput(renderingContext.getRenderedOutput());
            component.setSelfRendered(true);
        } catch (Throwable e) {
            if (ViewLifecycle.isStrict()) {
                LOG.warn("Error rendering component during lifecycle phase " + getElementState()
                        + " falling back to higher level rendering", e);
            } else if (ViewLifecycle.isTrace() && LOG.isDebugEnabled()) {
                LOG.debug("component rendering failed during lifecycle phase " + getElementState()
                        + " falling back to higher level rendering", e);
            }
        }

    }
}
