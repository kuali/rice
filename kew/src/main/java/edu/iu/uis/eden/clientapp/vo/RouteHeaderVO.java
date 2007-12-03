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
package edu.iu.uis.eden.clientapp.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.iu.uis.eden.engine.node.KeyValuePair;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.util.Utilities;

/**
 * Transport object for the {@link DocumentRouteHeaderValue}.  Represents a document to the
 * client programmer
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 * @workflow.webservice-object
 */
public class RouteHeaderVO implements Serializable {
    private static final Logger LOG = Logger.getLogger(RouteHeaderVO.class);

    static final long serialVersionUID = -677289794727007572L;

    private Long routeHeaderId;
    private String docRouteStatus;
    private Calendar dateCreated;
    private Calendar dateLastModified;
    private Calendar dateApproved;
    private Calendar dateFinalized;
    //private String docContent;
    private String docTitle;
    private String appDocId;
    private String overrideInd;
    private UserVO initiator;
    private UserVO routedByUser;
    private Integer docRouteLevel;
    //private String[] nodeNames;
    private String currentRouteNodeNames;
    private Integer docVersion;
    /**
     * @deprecated this is unreliable user docTypeId to retrieve document type
     */
    private String docTypeName;
    private String documentUrl;
    private boolean fyiRequested;
    private boolean ackRequested;
    private boolean approveRequested;
    private boolean completeRequested;
    private boolean userBlanketApprover;
    private Long docTypeId;
    //private DocumentContentVO documentContent = new DocumentContentVO();
    private ValidActionsVO validActions;

    //** Modify for adding notes to web service. Modify Date: April 7, 2006
    private NoteVO[] notes = null;
    private NoteVO[] notesToDelete = null;
    //** Modify ends

    /**
     * Probably needs to be an array for web services
     */
    private List variables = new ArrayList();

    public RouteHeaderVO() { }

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

    public UserVO getInitiator() {
        return initiator;
    }

    public void setInitiator(UserVO initiator) {
        this.initiator = initiator;
    }

    public UserVO getRoutedByUser() {
        return routedByUser;
    }

    public void setRoutedByUser(UserVO routedByUser) {
        this.routedByUser = routedByUser;
    }

    public String getOverrideInd() {
        return overrideInd;
    }

    public void setOverrideInd(String overrideInd) {
        this.overrideInd = overrideInd;
    }

    public Long getRouteHeaderId() {
        return routeHeaderId;
    }

    public void setRouteHeaderId(Long routeHeaderId) {
        this.routeHeaderId = routeHeaderId;
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


    public Long getDocTypeId() {
        return docTypeId;
    }

    public void setDocTypeId(Long docTypeId) {
        this.docTypeId = docTypeId;
    }


   //  ** Modify for adding notes to web service. Modify Date: April 7, 2006
    public NoteVO[] getNotes() {
		return notes;
	}

	public void setNotes(NoteVO[] notes) {
		this.notes = notes;
	}

	public NoteVO[] getNotesToDelete() {
		return notesToDelete;
	}

	public void setNotesToDelete(NoteVO[] notesToDelete) {
		this.notesToDelete = notesToDelete;
	}
	// ** Modify Ends

    private KeyValuePair findVariable(String name) {
        Iterator it = variables.iterator();
        while (it.hasNext()) {
            KeyValuePair kvp = (KeyValuePair) it.next();
            if (Utilities.equals(kvp.getKey(), name)) {
                return kvp;
            }
        }
        return null;
    }
    public String getVariable(String name) {
        KeyValuePair kvp = findVariable(name);
        if (kvp == null) return null;
        return kvp.getValue();
    }

    public void setVariable(String name, String value) {
        KeyValuePair kvp = findVariable(name);
        LOG.debug("# variables: " + variables.size());
        if (kvp == null) {
            if (value == null) {
                LOG.debug("null value set for variable: " + name);
                return;
            }
            LOG.debug("adding variable: '" + name + "'='" + value + "'");
            kvp = new KeyValuePair();
            kvp.setKey(name);
            kvp.setValue(value);
            variables.add(kvp);
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
                LOG.debug("setting variable '" + name + "' to value '" + value + "'");
                kvp.setValue(value);
            //}
        }
    }

    public List getVariables() {
        return variables;
    }

    public ValidActionsVO getValidActions() {
        return validActions;
    }

    public void setValidActions(ValidActionsVO validActions) {
        this.validActions = validActions;
    }
}