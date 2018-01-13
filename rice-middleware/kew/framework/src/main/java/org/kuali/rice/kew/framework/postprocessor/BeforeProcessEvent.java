/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.kew.framework.postprocessor;

import org.kuali.rice.kew.framework.postprocessor.IDocumentEvent;

/**
 * Event sent to the postprocessor when the processor is started
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BeforeProcessEvent implements IDocumentEvent {

	private static final long serialVersionUID = 2945081851810845704L;
	private String documentId;
	private String nodeInstanceId;
	private String appDocId;

	public BeforeProcessEvent(String documentId, String appDocId, String nodeInstanceId) {
		this.documentId = documentId;
		this.appDocId = appDocId;
		this.nodeInstanceId = nodeInstanceId;
	}
	
	public String getNodeInstanceId() {
	    return nodeInstanceId;
	}

	public String getDocumentId() {
		return documentId;
	}

	public String getAppDocId() {
		return appDocId;
	}

    public String getDocumentEventCode() {
        return IDocumentEvent.BEFORE_PROCESS;
    }

}
