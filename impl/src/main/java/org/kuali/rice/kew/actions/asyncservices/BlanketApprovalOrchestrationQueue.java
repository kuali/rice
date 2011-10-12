/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.actions.asyncservices;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;

import java.util.Set;

/**
 * Defines the contract for a message queue which handles orchestrating documents through the blanket approval process.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface BlanketApprovalOrchestrationQueue {
	
	void doBlanketApproveWork(String documentId, String principalId, String actionTakenId, Set<String> nodeNames) throws RiceIllegalArgumentException;
	
	void doBlanketApproveWork(String documentId, String principalId, String actionTakenId, Set<String> nodeNames, boolean shouldSearchIndex) throws RiceIllegalArgumentException;

}
