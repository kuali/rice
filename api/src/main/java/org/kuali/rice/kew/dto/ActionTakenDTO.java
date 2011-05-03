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
package org.kuali.rice.kew.dto;

import java.io.Serializable;
import java.util.Calendar;

/**
 * A transport object representing an ActionTakenValue
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class ActionTakenDTO implements Serializable {
    static final long serialVersionUID = -8818100923517546091L;
    private Long actionTakenId;
    private String documentId;
    private Integer docVersion;
    private String principalId;
    private String delegatorPrincpalId;
    private String delegatorGroupId;
    private String actionTaken;
    private Calendar actionDate;
    private String annotation = null;
    private ActionRequestDTO[] actionRequests = new ActionRequestDTO[0];

    public ActionTakenDTO() {
    }

    public ActionRequestDTO[] getActionRequests() {
		return this.actionRequests;
	}

	public void setActionRequests(ActionRequestDTO[] actionRequests) {
		this.actionRequests = actionRequests;
	}

	public Calendar getActionDate() {
        return actionDate;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public Long getActionTakenId() {
        return actionTakenId;
    }

    public String getAnnotation() {
        return annotation;
    }

    public Integer getDocVersion() {
        return docVersion;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setDocVersion(Integer docVersion) {
        this.docVersion = docVersion;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public void setActionTakenId(Long actionTakenId) {
        this.actionTakenId = actionTakenId;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public void setActionDate(Calendar actionDate) {
        this.actionDate = actionDate;
    }

	public String getPrincipalId() {
		return this.principalId;
	}

	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}

	public String getDelegatorPrincpalId() {
		return this.delegatorPrincpalId;
	}

	public void setDelegatorPrincpalId(String delegatorPrincpalId) {
		this.delegatorPrincpalId = delegatorPrincpalId;
	}

	public String getDelegatorGroupId() {
		return this.delegatorGroupId;
	}

	public void setDelegatorGroupId(String delegatorGroupId) {
		this.delegatorGroupId = delegatorGroupId;
	}

}
