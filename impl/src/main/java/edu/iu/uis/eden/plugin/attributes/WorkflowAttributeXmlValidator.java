/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.plugin.attributes;

import java.util.List;

import edu.iu.uis.eden.routetemplate.WorkflowAttributeValidationError;

/**
 * An interface which can be implemented by a {@link WorkflowAttribute} to allow for
 * validation of client routing data.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface WorkflowAttributeXmlValidator {

    /**
     * called after client has set properties on the attribute for 'xmlizing'.  Returns 
     * @return List of WorkflowAttributeValidationError objects
     */
    public List<WorkflowAttributeValidationError> validateClientRoutingData();
    
}
