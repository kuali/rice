/*
 * Copyright 2008-2009 The Kuali Foundation
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
