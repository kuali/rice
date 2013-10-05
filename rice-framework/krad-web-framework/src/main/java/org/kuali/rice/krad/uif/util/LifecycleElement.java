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
package org.kuali.rice.krad.uif.util;

/**
 * Interface to be implemented by objects that participates in the view lifecycle. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface LifecycleElement {

    /**
     * Determine if this layout manager is mutable.
     * 
     * <p>
     * Most layout managers are immutable, and all are immutable expect during initialization
     * and the during the view lifecycle. Those that have been copied within the view lifecycle,
     * however, may be modified during the same lifecycle.
     * </p>
     * 
     * @return True if the component is mutable.
     */
    boolean isMutable(boolean legalBeforeConfiguration);
    
    /**
     * Check for mutability on the component before modifying state.
     * 
     * @param legalDuringInitialization True if the operation is legal during view configuration,
     *        false if the operation is part of the component lifecycle.
     * @throws IllegalStateException If the component is not mutable.
     */
    void checkMutable(boolean legalDuringInitialization);

    /**
     * Copy the object
     *
     * @return the copied object
     */
    public <T> T copy();

}
