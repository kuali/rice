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
package edu.iu.uis.eden.actionlist;

import java.io.Serializable;
import java.util.Date;

import edu.iu.uis.eden.EdenConstants;

/**
 * model for the action list filter preferences
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionListFilter implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -365729646389290478L;
	private String filterLegend;
    private String documentTitle = "";
    private boolean excludeDocumentTitle;
    private String docRouteStatus = EdenConstants.ALL_CODE;
    private boolean excludeRouteStatus;
    private String actionRequestCd = EdenConstants.ALL_CODE;
    private boolean excludeActionRequestCd;
    private Long workgroupId;
    private String workgroupIdString = EdenConstants.NO_FILTERING;
    private String workgroupName = "";
    private boolean excludeWorkgroupId;
    private String documentType = "";
    private boolean excludeDocumentType;
    private Date createDateFrom;
    private Date createDateTo;
    private boolean excludeCreateDate;
    private Date lastAssignedDateFrom;
    private Date lastAssignedDateTo;
    private boolean excludeLastAssignedDate;
    private String delegatorId = "";
    private boolean excludeDelegatorId;
    private String delegationType;
    private boolean excludeDelegationType;
    private boolean filterOn;
    
    public String getActionRequestCd() {
        return actionRequestCd;
    }
    public void setActionRequestCd(String actionRequestCd) {
        this.actionRequestCd = actionRequestCd;
    }
    public Date getCreateDateFrom() {
        return createDateFrom;
    }
    public void setCreateDateFrom(Date createDate) {
        this.createDateFrom = createDate;
    }
    public String getDocRouteStatus() {
        return docRouteStatus;
    }
    public void setDocRouteStatus(String docRouteStatus) {
        this.docRouteStatus = docRouteStatus;
    }
    public String getDocumentTitle() {
        return documentTitle;
    }
    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }
    public String getDocumentType() {
        return documentType;
    }
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    public boolean isExcludeCreateDate() {
        return excludeCreateDate;
    }
    public void setExcludeCreateDate(boolean excludeCreateDate) {
        this.excludeCreateDate = excludeCreateDate;
    }
    public boolean isExcludeDocumentType() {
        return excludeDocumentType;
    }
    public void setExcludeDocumentType(boolean excludeDocument) {
        this.excludeDocumentType = excludeDocument;
    }
    public boolean isExcludeDocumentTitle() {
        return excludeDocumentTitle;
    }
    public void setExcludeDocumentTitle(boolean excludeDocumentTitle) {
        this.excludeDocumentTitle = excludeDocumentTitle;
    }
    public boolean isExcludeLastAssignedDate() {
        return excludeLastAssignedDate;
    }
    public void setExcludeLastAssignedDate(boolean excludeLastAssignedDate) {
        this.excludeLastAssignedDate = excludeLastAssignedDate;
    }
    public boolean isExcludeActionRequestCd() {
        return excludeActionRequestCd;
    }
    public void setExcludeActionRequestCd(boolean excludeRequestCd) {
        this.excludeActionRequestCd = excludeRequestCd;
    }
    public boolean isExcludeRouteStatus() {
        return excludeRouteStatus;
    }
    public void setExcludeRouteStatus(boolean excludeRouteStatus) {
        this.excludeRouteStatus = excludeRouteStatus;
    }
    public boolean isExcludeWorkgroupId() {
        return excludeWorkgroupId;
    }
    public void setExcludeWorkgroupId(boolean excludeWorkgroupId) {
        this.excludeWorkgroupId = excludeWorkgroupId;
    }
    public Date getLastAssignedDateTo() {
        return lastAssignedDateTo;
    }
    public void setLastAssignedDateTo(Date lastAssignedDate) {
        this.lastAssignedDateTo = lastAssignedDate;
    }
    public Long getWorkgroupId() {
        return workgroupId;
    }
    public void setWorkgroupId(Long workgroupId) {
        this.workgroupId = workgroupId;
    }
    public Date getCreateDateTo() {
        return createDateTo;
    }
    public void setCreateDateTo(Date createDateTo) {
        this.createDateTo = createDateTo;
    }
    public Date getLastAssignedDateFrom() {
        return lastAssignedDateFrom;
    }
    public void setLastAssignedDateFrom(Date lastAssignedDateFrom) {
        this.lastAssignedDateFrom = lastAssignedDateFrom;
    }
    public String getDelegatorId() {
        return delegatorId;
    }
    public void setDelegatorId(String delegatorId) {
        this.delegatorId = delegatorId;
    }

    public String getWorkgroupName() {
        return workgroupName;
    }

    public void setWorkgroupName(String workgroupName) {
        this.workgroupName = workgroupName;
    }

    public String getFilterLegend() {
        return filterLegend;
    }

    public void setFilterLegend(String filterLegend) {
        this.filterLegend = filterLegend;
    }

    public String getWorkgroupIdString() {
        return workgroupIdString;
    }

    public void setWorkgroupIdString(String workgroupIdString) {
        this.workgroupIdString = workgroupIdString;
    }

    public boolean isExcludeDelegatorId() {
        return excludeDelegatorId;
    }

    public void setExcludeDelegatorId(boolean excludeDelegatorId) {
        this.excludeDelegatorId = excludeDelegatorId;
    }

    public String getDelegationType() {
        return delegationType;
    }

    public void setDelegationType(String delegationType) {
        this.delegationType = delegationType;
    }

    public boolean isExcludeDelegationType() {
        return excludeDelegationType;
    }

    public void setExcludeDelegationType(boolean excludeDelegationType) {
        this.excludeDelegationType = excludeDelegationType;
    }

    public boolean isFilterOn() {
        return filterOn;
    }

    public void setFilterOn(boolean filterOn) {
        this.filterOn = filterOn;
    }
}