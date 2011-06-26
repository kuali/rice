/*
 * Copyright 2005-2009, 2011 The Kuali Foundation
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
package org.kuali.rice.kew.api.document;

/**
 * TODO... annotate this for JAXB
 */
public final class WorkflowAttributeValidationError {

    private final String key;
    private final String message;
    
    public WorkflowAttributeValidationError() {
    	this.key = null;
    	this.message = null;
    }
    
    public WorkflowAttributeValidationError(String key, String message) {
        this.key = key;
        this.message = message;
    }
    
    public String getKey() {
        return key;
    }

    public String getMessage() {
        return message;
    }

}
