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
import java.util.Collections;
import java.util.List;

import org.kuali.RiceConstants;
import org.kuali.core.document.Document;
import org.kuali.core.document.DocumentBase;
import org.kuali.core.util.documentserializer.PropertySerializabilityEvaluator;

/**
 * This object allows for grouping of related {@link WorkflowProperty} objects.  It defines a base path to which all {@link WorkflowProperty} are
 * relative. See {@link #getBasePath()} for a explanation of the meaning of the base path
 */
public class WorkflowPropertyGroup {
    private String basePath;
    private List<WorkflowProperty> workflowProperties;
    
    /**
     * Default constructor, sets the basePath to an empty string and an empty collection of {@link WorkflowProperty} objects.
     * 
     */
    public WorkflowPropertyGroup() {
        basePath = RiceConstants.EMPTY_STRING;
        workflowProperties = new ArrayList<WorkflowProperty>();
    }
    
    /**
     * Adds a {@link WorkflowProperty} element to this group
     * 
     * @param workflowProperty must be non-null
     */
    public void addWorkflowProperty(WorkflowProperty workflowProperty) {
        if (workflowProperty == null) {
            throw new IllegalArgumentException("WorkflowProperty is null");
        }
        workflowProperties.add(workflowProperty);
    }
    
    /**
     * Returns the list of added {@link WorkflowProperty} objects.
     * 
     * @return an unmodifiable list (but its elements are modifiable, but they should not be modified)
     */
    public List<WorkflowProperty> getWorkflowProperties() {
        return Collections.unmodifiableList(workflowProperties);
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
}
