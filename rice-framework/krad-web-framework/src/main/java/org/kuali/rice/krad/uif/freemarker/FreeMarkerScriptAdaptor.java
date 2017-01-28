/**
 * Copyright 2005-2017 The Kuali Foundation
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

import java.io.IOException;
import java.io.Serializable;

import org.kuali.rice.krad.uif.component.Component;
import org.springframework.util.StringUtils;

import freemarker.core.Environment;
import freemarker.core.InlineTemplateAdaptor;
import freemarker.template.TemplateException;

/**
 * Inline FreeMarker template adaptor for supporting script.ftl 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FreeMarkerScriptAdaptor implements InlineTemplateAdaptor, Serializable {

    private static final long serialVersionUID = 1675270336764602352L;

    /**
     * Render a script template inline.
     * 
     * {@inheritDoc}
     */
    @Override
    public void accept(Environment env) throws TemplateException, IOException {
        String script = FreeMarkerInlineRenderUtils.resolve(env, "value", String.class);

        if (!StringUtils.hasText(script)) {
            return;
        }

        Component component = FreeMarkerInlineRenderUtils.resolve(env, "component", Component.class);
        String role = FreeMarkerInlineRenderUtils.resolve(env, "role", String.class);
        FreeMarkerInlineRenderUtils.renderScript(script, component, role, env.getOut());
    }

}
