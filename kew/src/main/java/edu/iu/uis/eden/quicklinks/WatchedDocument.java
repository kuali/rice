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
package edu.iu.uis.eden.quicklinks;

/**
 * Represents a document that is being watched from the Quick Links.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WatchedDocument {
    private String documentHeaderId;
    private String documentStatusCode;
    private String documentTitle;
    public WatchedDocument(String documentHeaderId, String documentStatusCode, String documentTitle) {
        this.documentHeaderId = documentHeaderId;
        this.documentStatusCode = documentStatusCode;
        this.documentTitle = documentTitle;
    }
    
    public String getDocumentHeaderId() {
        return documentHeaderId;
    }
    public void setDocumentHeaderId(String documentHeaderId) {
        this.documentHeaderId = documentHeaderId;
    }
    public String getDocumentStatusCode() {
        return documentStatusCode;
    }
    public void setDocumentStatusCode(String documentStatusCode) {
        this.documentStatusCode = documentStatusCode;
    }
    public String getDocumentTitle() {
        return documentTitle;
    }
    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }
}
