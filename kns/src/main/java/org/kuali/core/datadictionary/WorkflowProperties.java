/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
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
package org.kuali.core.datadictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * An object that contains a list of {@link WorkflowPropertyGroup} objects
 */
public class WorkflowProperties {
    private List<WorkflowPropertyGroup> workflowPropertyGroups;
    
    public WorkflowProperties() {
        workflowPropertyGroups = new ArrayList<WorkflowPropertyGroup>();
    }
    
    public void addWorkflowPropertyGroup(WorkflowPropertyGroup workflowPropertyGroup) {
        this.workflowPropertyGroups.add(workflowPropertyGroup);
    }

    /**
     * Returns a list of workflow property groups, which are used to determine which properties should be serialized when generating
     * routing XML
     * 
     * @return a list of {@link WorkflowPropertyGroup} objects, in the order in which they were added
     */
    public List<WorkflowPropertyGroup> getWorkflowPropertyGroups() {
        return this.workflowPropertyGroups;
    }
}
