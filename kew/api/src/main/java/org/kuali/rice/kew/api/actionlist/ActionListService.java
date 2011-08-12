/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.kew.api.actionlist;

import org.kuali.rice.kew.api.action.ActionItem;

import javax.jws.WebParam;
import java.util.List;

public interface ActionListService {

	// TODO add the following methods to this service
	
	public Integer getUserActionItemCount(
			@WebParam(name = "principalId") String principalId);

	public List<ActionItem> getAllActionItems(
			@WebParam(name = "documentId") String documentId);

	public List<ActionItem> getActionItems(
			@WebParam(name = "documentId") String documentId,
			@WebParam(name = "actionRequestedCodes") List<String> actionRequestedCodes);

	List<ActionItem> getActionItemsForPrincipal(
			@WebParam(name = "principalId") String principalId);
	
}
