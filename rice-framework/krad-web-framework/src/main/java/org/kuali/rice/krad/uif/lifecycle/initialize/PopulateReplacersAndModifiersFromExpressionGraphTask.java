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
package org.kuali.rice.krad.uif.lifecycle.initialize;

import java.util.List;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.PropertyReplacer;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;
import org.kuali.rice.krad.uif.modifier.ComponentModifier;

/**
 * Populate property values on the all property replacers and component modifiers from the
 * expression graph.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PopulateReplacersAndModifiersFromExpressionGraphTask extends ViewLifecycleTaskBase<Component> {

    /**
     * Default constructor.
     */
    public PopulateReplacersAndModifiersFromExpressionGraphTask() {
        super(Component.class);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void performLifecycleTask() {
        Component component = (Component) getElementState().getElement();

        // move expressions on property replacers and component modifiers
        List<PropertyReplacer> componentPropertyReplacers = component.getPropertyReplacers();
        if (componentPropertyReplacers != null) {
            for (PropertyReplacer replacer : componentPropertyReplacers) {
                ViewLifecycle.getExpressionEvaluator().populatePropertyExpressionsFromGraph(replacer, true);
            }
        }

        List<ComponentModifier> componentModifiers = component.getComponentModifiers();
        if (componentModifiers != null) {
            for (ComponentModifier modifier : component.getComponentModifiers()) {
                ViewLifecycle.getExpressionEvaluator().populatePropertyExpressionsFromGraph(modifier, true);
            }
        }
    }

}
