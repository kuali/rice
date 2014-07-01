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
package org.kuali.rice.krad.uif.lifecycle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.modifier.ComponentModifier;

/**
 * View lifecycle task to run component modifiers based on the lifecycle phase. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RunComponentModifiersTask extends ViewLifecycleTaskBase<Component> {

    /**
     * Default constructor.
     */
    public RunComponentModifiersTask() {
        super(Component.class);
    }

     /**
      * Runs any configured <code>ComponentModifiers</code> for the given component that match the
      * given run phase and who run condition evaluation succeeds
      * 
      * <p>
      * If called during the initialize phase, the performInitialization method will be invoked on
      * the <code>ComponentModifier</code> before running
      * </p>
      * 
     * {@inheritDoc}
     */
    @Override
    protected void performLifecycleTask() {
        Component component = (Component) getElementState().getElement();
        
        List<ComponentModifier> componentModifiers = component.getComponentModifiers();
        if (componentModifiers == null) {
            return;
        }

        Object model = ViewLifecycle.getModel();
        String runPhase = getElementState().getViewPhase();
        for (ComponentModifier modifier : component.getComponentModifiers()) {
            // if run phase is initialize, invoke initialize method on modifier first
            if (StringUtils.equals(runPhase, UifConstants.ViewPhases.INITIALIZE)) {
                modifier.performInitialization(model, component);
            }

            // check run phase matches
            if (StringUtils.equals(modifier.getRunPhase(), runPhase)) {
                // check condition (if set) evaluates to true
                boolean runModifier = true;
                if (StringUtils.isNotBlank(modifier.getRunCondition())) {
                    Map<String, Object> context = new HashMap<String, Object>();
                    context.put(UifConstants.ContextVariableNames.COMPONENT, component);
                    context.put(UifConstants.ContextVariableNames.VIEW, ViewLifecycle.getView());

                    String conditionEvaluation = ViewLifecycle.getExpressionEvaluator()
                            .evaluateExpressionTemplate(context, modifier.getRunCondition());
                    runModifier = Boolean.parseBoolean(conditionEvaluation);
                }

                if (runModifier) {
                    modifier.performModification(model, component);
                }
            }
        }
    }

}
