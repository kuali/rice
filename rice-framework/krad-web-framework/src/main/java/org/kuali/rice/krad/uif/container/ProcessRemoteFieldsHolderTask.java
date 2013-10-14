/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.uif.container;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.field.RemoteFieldsHolder;
import org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecycleTask;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase;

/**
 * Process any remote fields holder that might be in the containers items.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ProcessRemoteFieldsHolderTask extends AbstractViewLifecycleTask {

    /**
     * Constructor.
     * 
     * @param phase The initialize phase for this container.
     */
    public ProcessRemoteFieldsHolderTask(ViewLifecyclePhase phase) {
        super(phase);
    }

    /**
     * Invoke custom initialization based on the view helper.
     * 
     * @see ViewHelperService#
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecycleTask#performLifecycleTask()
     */
    @Override
    protected void performLifecycleTask() {
        Container container = (Container) getPhase().getComponent();
        
        if (container instanceof CollectionGroup) {
            // Collection items will be processed as the lines are built
            return;
        }

        List<Component> processedItems = new ArrayList<Component>();

        // check for holders and invoke to retrieve the remotable fields and translate
        // translated fields are placed into the container item list at the position of the holder
        for (Component item : container.getItems()) {
            if (item instanceof RemoteFieldsHolder) {
                List<InputField> translatedFields = ((RemoteFieldsHolder) item)
                        .fetchAndTranslateRemoteFields(container);
                processedItems.addAll(translatedFields);
            } else {
                processedItems.add(item);
            }
        }

        // updated container items
        container.setItems(processedItems);

        
        // invoke hook point for adding components through code
        ViewLifecycle.getHelper().addCustomContainerComponents(getPhase().getModel(),
                (Container) getPhase().getComponent());
    }

}
