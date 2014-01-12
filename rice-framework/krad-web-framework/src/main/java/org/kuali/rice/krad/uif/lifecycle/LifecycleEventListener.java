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

import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.view.View;

/**
 * Interface that must be implemented by components that wish to be notified of a lifecycle event.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface LifecycleEventListener {

    /**
     * Invoked on a component listener when an event occurs for the given event component.
     *
     * @param lifecycleEvent event that occurred
     * @param view view instance the lifecycle is being processed for
     * @param model object containing the model data
     * @param eventElement element instance the event occurred on/for
     */
    void processEvent(ViewLifecycle.LifecycleEvent lifecycleEvent, View view, Object model,
            LifecycleElement eventElement);
    
}
