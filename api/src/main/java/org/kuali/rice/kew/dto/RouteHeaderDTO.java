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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.core.util.ConcreteKeyValue;

/**
 * Transport object for the DocumentRouteHeaderValue.  Represents a document to the
 * client programmer
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RouteHeaderDTO implements Serializable {

    static final long serialVersionUID = -677289794727007572L;

    private String documentId;
    private String docRouteStatus;
    private Calendar dateCreated;
    private Calendar dateLastModified;
    private Calendar dateApproved;
    private Calendar dateFinalized;
    private String docTitle;
    private String appDocId;
    private String initiatorPrincipalId;
    private String routedByPrincipalId;
    private Integer docRouteLevel;
    private String currentRouteNodeNames;
    private Integer docVersion;
    private String docTypeName;
    private String documentUrl;
    private String appDocStatus;
    private Calendar appDocStatusDate;
    private boolean fyiRequested;
    private boolean ackRequested;
    private boolean approveRequested;
    private boolean completeRequested;
    private boolean userBlanketApprover;
    private String docTypeId;
    private ValidActionsDTO validActions;

    private NoteDTO[] notes = null;
    private NoteDTO[] notesToDelete = null;
    
//    private String docStatusPolicy;

    /**
     * Probably needs to be an array for web services
     */
    private List<KeyValue> variables = new ArrayList<KeyValue>();

    public RouteHeaderDTO() { }

    public String getCurrentRouteNodeNames() {
        return currentRouteNodeNames;
    }

    public void setCurrentRouteNodeNames(String currentRouteNodeNames) {
        this.currentRouteNodeNames = currentRouteNodeNames;
    }

    public String getAppDocId() {
        return appDocId;
    }

    public void setAppDocId(String appDocId) {
        this.appDocId = appDocId;
    }

    public Calendar getDateApproved() {
        return dateApproved;
    }

    public void setDateApproved(Calendar dateApproved) {
        this.dateApproved = dateApproved;
    }

    public Calendar getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Calendar dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Calendar getDateFinalized() {
        return dateFinalized;
    }

    public void setDateFinalized(Calendar dateFinalized) {
        this.dateFinalized = dateFinalized;
    }

    public Calendar getDateLastModified() {
        return dateLastModified;
    }

    public void setDateLastModified(Calendar dateLastModified) {
        this.dateLastModified = dateLastModified;
    }

    public Integer getDocRouteLevel() {
        return docRouteLevel;
    }

    public void setDocRouteLevel(Integer docRouteLevel) {
        this.docRouteLevel = docRouteLevel;
    }

    public String getDocRouteStatus() {
        return docRouteStatus;
    }

    public void setDocRouteStatus(String docRouteStatus) {
        this.docRouteStatus = docRouteStatus;
    }

    public String getAppDocStatus() {
        return appDocStatus;
    }

    public void setAppDocStatus(String appDocStatus) {
        this.appDocStatus = appDocStatus;
    }

    public Calendar getAppDocStatusDate() {
        return appDocStatusDate;
    }

    public void setAppDocStatusDate(Calendar date) {
        this.appDocStatusDate = date;
    }
    
//    public String getDocStatusPolicy() {
//        return docStatusPolicy;
//    }
//
//    public void setDocStatusPolicy(String policy) {
//        this.docStatusPolicy = policy;
//    }

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    /**
     * @deprecated this is unreliable user docTypeId to retrieve document type
     */
    public String getDocTypeName() {
        return docTypeName;
    }

    /**
     * @deprecated this is unreliable user docTypeId to retrieve document type
     */
    public void setDocTypeName(String docTypeName) {
        this.docTypeName = docTypeName;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public Integer getDocVersion() {
        return docVersion;
    }

    public void setDocVersion(Integer docVersion) {
        this.docVersion = docVersion;
    }

    public String getInitiatorPrincipalId() {
		return this.initiatorPrincipalId;
	}

	public void setInitiatorPrincipalId(String initiatorPrincipalId) {
		this.initiatorPrincipalId = initiatorPrincipalId;
	}

	public String getRoutedByPrincipalId() {
		return this.routedByPrincipalId;
	}

	public void setRoutedByPrincipalId(String routedByPrincipalId) {
		this.routedByPrincipalId = routedByPrincipalId;
	}

	public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public boolean isAckRequested() {
        return ackRequested;
    }
    public void setAckRequested(boolean ackRequested) {
        this.ackRequested = ackRequested;
    }
    public boolean isApproveRequested() {
        return approveRequested;
    }
    public void setApproveRequested(boolean approveRequested) {
        this.approveRequested = approveRequested;
    }
    public boolean isCompleteRequested() {
        return completeRequested;
    }
    public void setCompleteRequested(boolean completeRequested) {
        this.completeRequested = completeRequested;
    }
    public boolean isFyiRequested() {
        return fyiRequested;
    }
    public void setFyiRequested(boolean fyiRequested) {
        this.fyiRequested = fyiRequested;
    }
    public boolean isUserBlanketApprover() {
        return userBlanketApprover;
    }
    public void setUserBlanketApprover(boolean userBlanketApprover) {
        this.userBlanketApprover = userBlanketApprover;
    }


    public String getDocTypeId() {
        return docTypeId;
    }

    public void setDocTypeId(String docTypeId) {
        this.docTypeId = docTypeId;
    }


   //  ** Modify for adding notes to web service. Modify Date: April 7, 2006
    public NoteDTO[] getNotes() {
		return notes;
	}

	public void setNotes(NoteDTO[] notes) {
		this.notes = notes;
	}

	public NoteDTO[] getNotesToDelete() {
		return notesToDelete;
	}

	public void setNotesToDelete(NoteDTO[] notesToDelete) {
		this.notesToDelete = notesToDelete;
	}
	// ** Modify Ends

    private KeyValue findVariable(String name) {
    	for (KeyValue kvp : variables) {
            if (isEqual(kvp.getKey(), name)) {
                return kvp;
            }
        }
        return null;
    }
    public String getVariable(String name) {
        KeyValue kvp = findVariable(name);
        if (kvp == null) return null;
        return kvp.getValue();
    }

    public void setVariable(String name, String value) {
        final KeyValue kvp = findVariable(name);
        if (kvp == null) {
            if (value == null) {
                return;
            }
            variables.add(new ConcreteKeyValue(name, value));
        } else {
            
        	// values do not need to be removed from the VO
            // in fact they CAN'T be, as the DocumentRouteHeaderValue
            // must observe the null value so the removal actually
            // propagates (otherwise DocumentRouteHeaderValue can't
            // guess what you wanted to remove...the alternative is
            // to just reset all the vars wholesale but that requires
            // a slightly more complicated implementation (to distinguish
            // non-variable state in the state table) and is fraught
            // with peril in general)
            //if (value == null) {
            //    LOG.error("Removing value: " + kvp.getKey() + "=" + kvp.getValue());
            //    variables.remove(kvp);
            //} else {
        		variables.remove(kvp);
        		variables.add(new ConcreteKeyValue(kvp.getKey(), value));
            //}
        }
    }

    public List<KeyValue> getVariables() {
        return variables;
    }

    public ValidActionsDTO getValidActions() {
        return validActions;
    }

    public void setValidActions(ValidActionsDTO validActions) {
        this.validActions = validActions;
    }
    
    private boolean isEqual(String a, String b) {
        return ((a == null && b == null) || (a != null && a.equals(b)));
    }

}
