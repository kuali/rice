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
package org.kuali.rice.krad.uif.lifecycle;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.view.View;

/**
 * Interface for encapsulting the completed processing results for an view lifecycle process.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ViewLifecycleResult {

    /**
     * Get the view.
     * 
     * @return A copy of the original view passed in at the start of the lifecycle process, but with
     *         lifecycle processing applied.
     */
    View getProcessedView();
    
    /**
     * Get the component initialized by a refresh lifecycle.
     * 
     * @return A copy of the original component passed in for a refresh lifecycle.
     */
    <T extends Component> T getRefreshComponent();
    
}
