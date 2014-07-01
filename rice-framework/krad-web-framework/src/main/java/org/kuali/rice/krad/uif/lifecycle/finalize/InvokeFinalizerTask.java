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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MethodInvoker;

/**
 * Invoke finalizer methods on the component.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InvokeFinalizerTask extends ViewLifecycleTaskBase<Component> {
    
    private final Logger LOG = LoggerFactory.getLogger(InvokeFinalizerTask.class);

    /**
     * Default constructor.
     */
    public InvokeFinalizerTask() {
        super(Component.class);
    }

    /**
     * Invokes the finalize method for the component (if configured) and sets the render output for
     * the component to the returned method string (if method is not a void type)
     */
    @Override
    protected void performLifecycleTask() {
        Component component = (Component) getElementState().getElement();
        String finalizeMethodToCall = component.getFinalizeMethodToCall();
        MethodInvoker finalizeMethodInvoker = component.getFinalizeMethodInvoker();

        if (StringUtils.isBlank(finalizeMethodToCall) && (finalizeMethodInvoker == null)) {
            return;
        }

        if (finalizeMethodInvoker == null) {
            finalizeMethodInvoker = new MethodInvoker();
        }

        // if method not set on invoker, use finalizeMethodToCall, note staticMethod could be set(don't know since
        // there is not a getter), if so it will override the target method in prepare
        if (StringUtils.isBlank(finalizeMethodInvoker.getTargetMethod())) {
            finalizeMethodInvoker.setTargetMethod(finalizeMethodToCall);
        }

        // if target class or object not set, use view helper service
        if ((finalizeMethodInvoker.getTargetClass() == null) && (finalizeMethodInvoker.getTargetObject() == null)) {
            finalizeMethodInvoker.setTargetObject(ViewLifecycle.getHelper());
        }

        // setup arguments for method
        List<Object> additionalArguments = component.getFinalizeMethodAdditionalArguments();
        if (additionalArguments == null) {
            additionalArguments = new ArrayList<Object>();
        }

        Object[] arguments = new Object[2 + additionalArguments.size()];
        arguments[0] = component;
        arguments[1] = ViewLifecycle.getModel();

        int argumentIndex = 1;
        for (Object argument : additionalArguments) {
            argumentIndex++;
            arguments[argumentIndex] = argument;
        }
        finalizeMethodInvoker.setArguments(arguments);

        // invoke finalize method
        try {
            LOG.debug("Invoking finalize method: "
                    + finalizeMethodInvoker.getTargetMethod()
                    + " for component: "
                    + component.getId());
            finalizeMethodInvoker.prepare();

            Class<?> methodReturnType = finalizeMethodInvoker.getPreparedMethod().getReturnType();
            if (StringUtils.equals("void", methodReturnType.getName())) {
                finalizeMethodInvoker.invoke();
            } else {
                String renderOutput = (String) finalizeMethodInvoker.invoke();

                component.setSelfRendered(true);
                component.setRenderedHtmlOutput(renderOutput);
            }
        } catch (Exception e) {
            LOG.error("Error invoking finalize method for component: " + component.getId(), e);
            throw new RuntimeException("Error invoking finalize method for component: " + component.getId(), e);
        }
    }

}
