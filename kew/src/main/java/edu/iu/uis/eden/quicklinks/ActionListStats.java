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

import edu.iu.uis.eden.doctype.DocumentType;

/**
 * Stores statistics about the number of times a particular {@link DocumentType}
 * appears in a particular user's Action List.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionListStats implements Comparable {

    private String documentTypeName;
    private String documentTypeLabelText;
    private int count;

    public ActionListStats(String documentTypeName, String documentTypeLabelText, int count) {
        this.documentTypeName = documentTypeName;
        this.documentTypeLabelText = documentTypeLabelText;
        this.count = count;
    }
    
    public String getDocumentTypeLabelText() {
        return documentTypeLabelText;
    }
    public void setDocumentTypeLabelText(String documentTypeLabelText) {
        this.documentTypeLabelText = documentTypeLabelText;
    }
    public String getDocumentTypeName() {
        return documentTypeName;
    }
    public void setDocumentTypeName(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public String getActionListFilterUrl() {
        return "ActionList.do?method=showRequestFilteredView&docType=" + documentTypeName;
    }
    
    public int compareTo(Object obj) {
        if (obj != null && obj instanceof ActionListStats) {
            return this.documentTypeLabelText.compareTo(((ActionListStats)obj).documentTypeLabelText);
        }
        return 0;
    }
}
