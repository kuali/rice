/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.krad.uif.history;

import java.io.Serializable;

/**
 * A simple object that keeps track of various HistoryInformation
 * 
 * TODO a variety of these settings are not used in the current implementation of breadcrumbs
 * and history, they may be removed later if they prove unuseful in future changes
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class HistoryEntry implements Serializable{
    private static final long serialVersionUID = -8310916657379268794L;
    private String viewId;
    private String pageId;
    private String title;
    private String url;
    private String formKey;

    public HistoryEntry(){
        
    }
    
    /**
     * This constructs a ...
     * 
     * @param viewId
     * @param pageId
     * @param title
     * @param url
     * @param formKey
     */
    public HistoryEntry(String viewId, String pageId, String title, String url, String formKey) {
        super();
        this.viewId = viewId;
        this.pageId = pageId;
        this.title = title;
        this.url = url;
        this.formKey = formKey;
    }
    
    public String toParam(){
        return viewId + History.VAR_TOKEN 
            + pageId + History.VAR_TOKEN 
            + title + History.VAR_TOKEN 
            + url + History.VAR_TOKEN 
            + formKey;
    }

    /**
     * The viewId of the view
     * @return the viewId
     */
    public String getViewId() {
        return this.viewId;
    }

    /**
     * @param viewId the viewId to set
     */
    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    /**
     * The pageId of the page on the view
     * @return the pageId
     */
    public String getPageId() {
        return this.pageId;
    }

    /**
     * @param pageId the pageId to set
     */
    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    /**
     * The title of the view
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * The url of this HistoryEntry
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the formKey
     */
    public String getFormKey() {
        return this.formKey;
    }

    /**
     * The formKey of the form in the view
     * TODO unsure of use
     * @param formKey the formKey to set
     */
    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }
    
    
    
}
