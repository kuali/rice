package org.kuali.rice.kew.webservice;

import java.util.List;

/**
 * Response object used when creating or obtaining documents
 */
public class DocumentResponse extends StandardResponse {
    protected String docId;
    protected String docContent;
    protected String title;
    protected List<NoteDetail> notes;
    protected String actionRequested;
    public DocumentResponse() {}
    public DocumentResponse(StandardResponse standardResponse) {
        this.appDocId = standardResponse.getAppDocId();
        this.createDate = standardResponse.getCreateDate();
        this.docStatus = standardResponse.getDocStatus();
        this.errorMessage = standardResponse.getErrorMessage();
        this.initiatorPrincipalId = standardResponse.getInitiatorPrincipalId();
        this.initiatorName = standardResponse.getInitiatorName();
        this.routedByPrincipalId = standardResponse.getRoutedByPrincipalId();
        this.routedByUserName = standardResponse.getRoutedByUserName();
    }
    public String getDocContent() {
        return docContent;
    }
    public void setDocContent(String docContent) {
        this.docContent = docContent;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public List<NoteDetail> getNotes() {
        return notes;
    }
    public void setNotes(List<NoteDetail> notes) {
        this.notes = notes;
    }
    public String getActionRequested() {
        return actionRequested;
    }
    public void setActionRequested(String actionRequested) {
        this.actionRequested = actionRequested;
    }
    public String getDocId() {
        return docId;
    }
    public void setDocId(String docId) {
        this.docId = docId;
    }
}