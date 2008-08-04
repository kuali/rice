/*
 * Copyright 2007 The Kuali Foundation.
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

package org.kuali.core.datadictionary.control;

/**
 *                         The workflowWorkgroup element control is used to identify
                        the field as being a Workgroup Name field.  The magnifying
                        glass will do a WorkGroup Lookup into the workflow system.
                        The Workgroup Name will be returned from the lookup.

                        This control also displays some special icons next to the
                        magnifying glass.

 */
public class WorkflowWorkgroupControlDefinition extends ControlDefinitionBase {

    public WorkflowWorkgroupControlDefinition() {
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinitionBase#isWorkgroup()
     */
    @Override
    public boolean isWorkflowWorkgroup() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "WorkflowWorkgroupControlDefinition";
    }
}