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
