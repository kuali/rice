/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.web.ui;

import java.io.Serializable;
import java.util.List;


/**
 * 
 */
public class ResultRow implements Serializable {
    private static final long serialVersionUID = 2880508981008533913L;
    private List<Column> columns;
    private String returnUrl;
    private String actionUrls;
    private String objectId;

    public ResultRow(List<Column> columns, String returnUrl, String actionUrls) {
        this.columns = columns;
        this.returnUrl = returnUrl;
        this.actionUrls = actionUrls;
    }

    /**
     * @return Returns the returnUrl.
     */
    public String getReturnUrl() {
        return returnUrl;
    }

    /**
     * @param returnUrl The returnUrl to set.
     */
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    /**
     * @return Returns the columns.
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * @param columns The columns to set.
     */
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    /**
     * @return Returns the actions url
     */
    public String getActionUrls() {
        return actionUrls;
    }

    /**
     * @param actionsUrl the actions url
     */
    public void setActionUrls(String actionUrls) {
        this.actionUrls = actionUrls;
    }

    /**
     * Gets the Object ID of the BO that this row represents
     * @return
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * Sets the Object ID of the BO that this row represents
     * @param objectId
     */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
