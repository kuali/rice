/*
 * Copyright 2005-2009 The Kuali Foundation
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
package org.kuali.rice.kew.actionrequest.service;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;

/**
 * Defines the contract for a message queue which "refreshes" a document at it's current node.  The refresh process will
 * delete all pending action requests at the current node(s) on the document and then send the document back through at
 * it's current node(s) so requests can be regenerated.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentRefreshQueue {

	void requeueDocument(String documentId) throws RiceIllegalArgumentException;
	
}
