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
package org.kuali.rice.krad.uif.lifecycle.model;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.component.ClientSideState;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;
import org.kuali.rice.krad.uif.util.CopyUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;

/**
 * Synchronize client side state for the component.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SyncClientSideStateTask extends ViewLifecycleTaskBase<Component> {

    /**
     * Default constructor.
     */
    public SyncClientSideStateTask() {
        super(Component.class);
    }

    /**
     * Updates the properties of the given component instance with the value found from the
     * corresponding map of client state (if found)
     * 
     * {@inheritDoc}
     */
    @Override
    protected void performLifecycleTask() {
        Component component = (Component) getElementState().getElement();
        ViewModel model = (ViewModel) ViewLifecycle.getModel();

        // find the map of state that was sent for component (if any)
        Map<String, Object> clientSideState = model.getClientStateForSyncing();
        if (!(component instanceof View) && clientSideState.containsKey(component.getId())) {
            @SuppressWarnings("unchecked")
            Map<String, Object> componentState =
                    (Map<String, Object>) clientSideState.get(component.getId());
            clientSideState = componentState;
        }

        // if state was sent, match with fields on the component that are annotated to have client state
        if ((clientSideState != null) && (!clientSideState.isEmpty())) {
            Map<String, Annotation> annotatedFields = CopyUtils.getFieldsWithAnnotation(component.getClass(),
                    ClientSideState.class);

            for (Entry<String, Annotation> annotatedField : annotatedFields.entrySet()) {
                ClientSideState clientSideStateAnnot = (ClientSideState) annotatedField.getValue();

                String variableName = clientSideStateAnnot.variableName();
                if (StringUtils.isBlank(variableName)) {
                    variableName = annotatedField.getKey();
                }

                if (clientSideState.containsKey(variableName)) {
                    Object value = clientSideState.get(variableName);
                    ObjectPropertyUtils.setPropertyValue(component, annotatedField.getKey(), value);
                }
            }
        }
    }

}
