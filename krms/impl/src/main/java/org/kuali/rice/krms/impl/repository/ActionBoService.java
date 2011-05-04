/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.krms.impl.repository;

import java.util.List;

import org.kuali.rice.krms.api.repository.action.ActionAttribute;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;

/**
 * This is the interface for accessing KRMS repositiory Action related bos 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface ActionBoService {
	public void createAction(ActionDefinition action);
	public void updateAction(ActionDefinition action);
	
	public ActionDefinition getActionByActionId(String actionId);
	public ActionDefinition getActionByNameAndNamespace(String name, String namespace);
	public List<ActionDefinition> getActionsByRuleId(String ruleId);
	public ActionDefinition getActionByRuleIdAndSequenceNumber(String ruleId, Integer sequenceNumber);
	
	public void createActionAttribute(ActionAttribute actionAttribute);
	public void updateActionAttribute(ActionAttribute actionAttribute);
	
	
//	/**
//	* Converts a mutable bo to it's immutable counterpart
//	* @param bo the mutable business object
//	* @return the immutable object
//	*/
//	public ActionDefinition to( ActionBo bo);
//
//   /**
//	* Converts a immutable object to it's mutable bo counterpart
//	* @param im immutable object
//	* @return the mutable bo
//	*/
//	public ActionBo from( ActionDefinition im );
}
