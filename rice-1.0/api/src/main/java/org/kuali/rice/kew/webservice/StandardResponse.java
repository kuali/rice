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

/**
 * "Standard" response object
 */
public class StandardResponse extends ErrorResponse {
    protected String docStatus;
    protected String createDate;
    protected String initiatorPrincipalId;
    protected String routedByPrincipalId;
    protected String routedByUserName;
    protected String appDocId;
    protected String initiatorName;
    public String getDocStatus() {
        return docStatus;
    }
    public void setDocStatus(String docStatus) {
        this.docStatus = docStatus;
    }
    public String getCreateDate() {
        return createDate;
    }
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    public String getInitiatorPrincipalId() {
        return initiatorPrincipalId;
    }
    public void setInitiatorPrincipalId(String initiatorId) {
        this.initiatorPrincipalId = initiatorId;
    }
    public String getAppDocId() {
        return appDocId;
    }
    public void setAppDocId(String appDocId) {
        this.appDocId = appDocId;
    }
    public String getInitiatorName() {
        return initiatorName;
    }
    public void setInitiatorName(String initiatorName) {
        this.initiatorName = initiatorName;
    }
    public String getRoutedByPrincipalId() {
        return routedByPrincipalId;
    }
    public void setRoutedByPrincipalId(String routedByUserId) {
        this.routedByPrincipalId = routedByUserId;
    }
    public String getRoutedByUserName() {
        return routedByUserName;
    }
    public void setRoutedByUserName(String routedByUserName) {
        this.routedByUserName = routedByUserName;
    }
}
