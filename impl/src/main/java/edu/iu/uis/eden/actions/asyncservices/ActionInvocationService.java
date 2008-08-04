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
package edu.iu.uis.eden.actions.asyncservices;

import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Interface for defining the contract for the remoting of ActionInvocationProcessor.  
 * Created when the ActionInvocationProcessor went to a service.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface ActionInvocationService {

	public void invokeAction(WorkflowUser user, Long documentId, ActionInvocation invocation);
	
	
}
