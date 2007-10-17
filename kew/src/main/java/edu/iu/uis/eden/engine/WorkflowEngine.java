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
package edu.iu.uis.eden.engine;

import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;

/**
 * Defines the contract to the core workflow engine.  The standard unit of work of the engine
 * is the process method.  Document must also be initialized by the WorkflowEngine when they
 * are initially created.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface WorkflowEngine {
        
    public void process(Long documentId, Long nodeInstanceId) throws Exception;
    
    public void initializeDocument(DocumentRouteHeaderValue document);
    	
}
