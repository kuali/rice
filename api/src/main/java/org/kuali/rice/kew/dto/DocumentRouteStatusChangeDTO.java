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
package org.kuali.rice.kew.dto;

/**
 * A document event representing the transition of a document from one status to another.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentRouteStatusChangeDTO extends DocumentEventDTO {

	private static final long serialVersionUID = 3969488320690393356L;
	private String newRouteStatus;
    private String oldRouteStatus;

    public DocumentRouteStatusChangeDTO() {
        super(ROUTE_STATUS_CHANGE);
    }

    public String getNewRouteStatus() {
        return newRouteStatus;
    }

    public void setNewRouteStatus(String newRouteStatus) {
        this.newRouteStatus = newRouteStatus;
    }

    public String getOldRouteStatus() {
        return oldRouteStatus;
    }

    public void setOldRouteStatus(String oldRouteStatus) {
        this.oldRouteStatus = oldRouteStatus;
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("DocumentId ").append(getDocumentId());
        buffer.append(" changing from routeStatus ").append(oldRouteStatus);
        buffer.append(" to routeStatus ").append(newRouteStatus);

        return buffer.toString();
    }

}
