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

/**
 * Represents a discrete task within the view lifecycle. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @param <T> top level lifecycle element type this task applies to
 */
public interface ViewLifecycleTask<T> extends Runnable {
    
    /**
     * Gets the top level lifecycle element type that this task applies to.
     * 
     * <p>
     * If an element is not a subclass of this type, then the task will not be performed on that
     * element.
     * </p>
     * 
     * @return lifecycle element type
     */
    Class<T> getElementType();

    /**
     * Gets the phase this lifecycle task is a part of.
     * 
     * @return lifecycle phase
     */
    LifecycleElementState getElementState();

    /**
     * @see #getElementState()
     */
    void setElementState(LifecycleElementState lifecycleElementState);
    
}
