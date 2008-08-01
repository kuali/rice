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
package org.kuali.core.datadictionary.conversion;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This object allows for grouping of related {@link WorkflowProperty} objects.  It defines a base path to which all {@link WorkflowProperty} are
 * relative. See {@link #getBasePath()} for a explanation of the meaning of the base path
 */
public class WorkflowPropertyGroup {
    private String basePath = KNSConstants.EMPTY_STRING;
    private List<WorkflowProperty> workflowProperties = new ArrayList<WorkflowProperty>();
    
    /**
     * Adds a {@link WorkflowProperty} element to this group
     * 
     * @param workflowProperty must be non-null
     */
    public void addWorkflowProperty(WorkflowProperty workflowProperty) {
        workflowProperties.add(workflowProperty);
    }
    
    /**
     * Returns the list of added {@link WorkflowProperty} objects.
     * 
     * @return an unmodifiable list (but its elements are modifiable, but they should not be modified)
     */
    public List<WorkflowProperty> getWorkflowProperties() {
        return workflowProperties;
    }

    /**
     * Returns the base path of the group, which represents the path that all {@link WorkflowProperty} objects are relative to.  The base path
     * itself should be relative from the object being serialized, which may not necessarily be the document, see {@link Document#wrapDocumentWithMetadataForXmlSerialization()}
     * and {@link Document#getBasePathToDocumentDuringSerialization()}
     * 
     * @return the base path
     */
    public String getBasePath() {
        return this.basePath;
    }

    /**
     * Sets the base path, for more details, see {@link #getBasePath()}
     * 
     * @param basePath see description of {@link #getBasePath()}
     */
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setWorkflowProperties(List<WorkflowProperty> workflowProperties) {
        this.workflowProperties = workflowProperties;
    }
}
