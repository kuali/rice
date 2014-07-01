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
package org.kuali.rice.krad.uif.lifecycle.finalize;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;
import org.kuali.rice.krad.uif.view.View;

/**
 * Add templates defined on this component to the view for rendering.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AddViewTemplatesTask extends ViewLifecycleTaskBase<Component> {

    /**
     * Default constructor.
     */
    public AddViewTemplatesTask() {
        super(Component.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performLifecycleTask() {
        Component component = (Component) getElementState().getElement();
        View view = ViewLifecycle.getView();

        // add the components template to the views list of components
        if (!component.isSelfRendered() && StringUtils.isNotBlank(component.getTemplate())) {
            String template = component.getTemplate();
            view.addViewTemplate(template);
            
            for (String additionalTemplate : component.getAdditionalTemplates()) {
                view.addViewTemplate(additionalTemplate);
            }
        }
    }

}
