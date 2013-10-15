/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.uif.lifecycle.model;

import java.util.List;
import java.util.UUID;

import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ComponentSecurity;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.component.PropertyReplacer;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.layout.LayoutManager;
import org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecycleTask;
import org.kuali.rice.krad.uif.lifecycle.ApplyModelComponentPhase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase;
import org.kuali.rice.krad.uif.modifier.ComponentModifier;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.View;

/**
 * Evaluate expressions for the component.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EvaluateExpressionsTask extends AbstractViewLifecycleTask {

    /**
     * Constructor.
     * 
     * @param phase The apply model phase for the component.
     */
    public EvaluateExpressionsTask(ViewLifecyclePhase phase) {
        super(phase);
    }

    /**
     * Checks against the visited ids to see if the id is duplicate, if so it is adjusted to make an
     * unique id by appending an unique identifier
     * 
     * @param id id to adjust if necessary
     * @param visitedIds tracks components ids that have been seen for adjusting duplicates
     * @return original or adjusted id
     */
    public String adjustIdIfNecessary(LifecycleElement element) {
        ApplyModelComponentPhase phase = (ApplyModelComponentPhase) getPhase();
        String id = element.getId();
        
        if (phase.visit(element)) {
            element.setId(id + '-' + UUID.randomUUID());
            boolean visitedAfter = phase.visit(element);
            assert !visitedAfter;
        }

        return id;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecycleTask#performLifecycleTask()
     */
    @Override
    protected void performLifecycleTask() {
        ExpressionEvaluator expressionEvaluator = ViewLifecycle.getHelper().getExpressionEvaluator();
        View view = ViewLifecycle.getView();
        Component component = getPhase().getComponent();

        List<PropertyReplacer> componentPropertyReplacers = component.getPropertyReplacers();
        if (componentPropertyReplacers != null) {
            for (PropertyReplacer replacer : componentPropertyReplacers) {
                expressionEvaluator.evaluateExpressionsOnConfigurable(view, replacer, component.getContext());
            }
        }

        List<ComponentModifier> componentModifiers = component.getComponentModifiers();
        if (componentModifiers != null) {
            for (ComponentModifier modifier : component.getComponentModifiers()) {
                expressionEvaluator.evaluateExpressionsOnConfigurable(view, modifier, component.getContext());
            }
        }

        expressionEvaluator.evaluateExpressionsOnConfigurable(view, component, component.getContext());
        
        // evaluate expressions on component security
        ComponentSecurity componentSecurity = component.getComponentSecurity();
        expressionEvaluator.evaluateExpressionsOnConfigurable(view, componentSecurity, component.getContext());

        // evaluate expressions on the binding info object
        if (component instanceof DataBinding) {
            BindingInfo bindingInfo = ((DataBinding) component).getBindingInfo();
            expressionEvaluator.evaluateExpressionsOnConfigurable(view, bindingInfo, component.getContext());
        }
        
        // set context evaluate expressions on the layout manager
        if (component instanceof Container) {
            LayoutManager layoutManager = ((Container) component).getLayoutManager();

            if (layoutManager != null) {
                expressionEvaluator.evaluateExpressionsOnConfigurable(view, layoutManager,
                        layoutManager.getContext());

                adjustIdIfNecessary(layoutManager);
            }
        }

        // TODO: is this needed?
        // adjustIdIfNecessary(component);
    }

}
