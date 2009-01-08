/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.kew.actionlist.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts.action.ActionForm;
import org.kuali.rice.kew.actionlist.ActionToTake;


/**
 * Struts form for action ActionListAction
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionListForm extends ActionForm {

    private static final long serialVersionUID = -6246391732337228007L;

    private String delegator;
    private String methodToCall = "";
    private String helpDeskActionListUserName;
    private String docType;
    private String filterLegend;
    private String actionListType;
    private Boolean customActionList;
    private String defaultActionToTake;
    private List actionsToTake = new ArrayList();
    private Map defaultActions = new HashMap();
    private String delegationId;
    private List delegators;
    private String primaryDelegateId;
    private List primaryDelegates;
    private Boolean hasCustomActions;
    private String routeLogPopup;
    private String documentPopup;

    // "sticky" parameters for paginated action list
    private Integer currentPage;
    private String currentSort;
    private String currentDir;

    // parameters for paginated action list
    private Integer page;
    private String sort;
    private String dir;

    private int count;
    private String cssFile = "screen.css";
    private String logoAlign = "left";
    private String viewOutbox;
    private Long[] outboxItems;
    private boolean outBoxEmpty;
    private Boolean showOutbox;

    public String getRouteLogPopup() {
	return routeLogPopup;
    }

    public void setRouteLogPopup(String routeLogPopup) {
	this.routeLogPopup = routeLogPopup;
    }

    public String getDocumentPopup() {
	return documentPopup;
    }

    public void setDocumentPopup(String documentPopup) {
	this.documentPopup = documentPopup;
    }

    public String getHelpDeskActionListUserName() {
	return helpDeskActionListUserName;
    }

    public void setHelpDeskActionListUserName(String helpDeskActionListUserName) {
	this.helpDeskActionListUserName = helpDeskActionListUserName;
    }

    public String getMethodToCall() {
	return methodToCall;
    }

    public void setMethodToCall(String methodToCall) {
	this.methodToCall = methodToCall;
    }

    public String getDelegator() {
	return delegator;
    }

    public void setDelegator(String delegator) {
	this.delegator = delegator;
    }

    public String getPrimaryDelegateId() {
		return this.primaryDelegateId;
	}

	public void setPrimaryDelegateId(String primaryDelegateId) {
		this.primaryDelegateId = primaryDelegateId;
	}

	public List getPrimaryDelegates() {
		return this.primaryDelegates;
	}

	public void setPrimaryDelegates(List primaryDelegates) {
		this.primaryDelegates = primaryDelegates;
	}

	public String getDocType() {
	return docType;
    }

    public void setDocType(String docType) {
	this.docType = docType;
    }

    public String getFilterLegend() {
	return filterLegend;
    }

    public void setFilterLegend(String filterLegend) {
	this.filterLegend = filterLegend;
    }

    public String getActionListType() {
	if (actionListType == null) {
	    setActionListType("all");
	}
	return actionListType;
    }

    public void setActionListType(String actionListType) {
	this.actionListType = actionListType;
    }

    public Boolean getCustomActionList() {
	return customActionList;
    }

    public void setCustomActionList(Boolean customActionList) {
	this.customActionList = customActionList;
    }

    public String getDefaultActionToTake() {
	return defaultActionToTake;
    }

    public void setDefaultActionToTake(String defaultActionToTake) {
	this.defaultActionToTake = defaultActionToTake;
    }

    public List getActionsToTake() {
	return actionsToTake;
    }

    public void setActionsToTake(List actionsToTake) {
	this.actionsToTake = actionsToTake;
    }

    public ActionToTake getActions(int index) {
	while (getActionsToTake().size() <= index) {
	    getActionsToTake().add(new ActionToTake());
	}
	return (ActionToTake) getActionsToTake().get(index);
    }

    public Map getDefaultActions() {
	return defaultActions;
    }

    public void setDefaultActions(Map defaultActions) {
	this.defaultActions = defaultActions;
    }

    public String getDelegationId() {
	return delegationId;
    }

    public void setDelegationId(String delegationId) {
	this.delegationId = delegationId;
    }

    public List getDelegators() {
	return delegators;
    }

    public void setDelegators(List delegators) {
	this.delegators = delegators;
    }

    public Boolean getHasCustomActions() {
	return hasCustomActions;
    }

    public void setHasCustomActions(Boolean hasCustomActions) {
	this.hasCustomActions = hasCustomActions;
    }

    public String getDir() {
	return dir;
    }

    public void setDir(String dir) {
	this.dir = dir;
    }

    public Integer getPage() {
	return page;
    }

    public void setPage(Integer page) {
	this.page = page;
    }

    public String getSort() {
	return sort;
    }

    public void setSort(String sort) {
	this.sort = sort;
    }

    public Integer getCurrentPage() {
	return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
	this.currentPage = currentPage;
    }

    public String getCurrentDir() {
	return currentDir;
    }

    public void setCurrentDir(String currentDir) {
	this.currentDir = currentDir;
    }

    public String getCurrentSort() {
	return currentSort;
    }

    public void setCurrentSort(String currentSort) {
	this.currentSort = currentSort;
    }

    public int getCount() {
	return count;
    }

    public void setCount(int count) {
	this.count = count;
    }

    public String getCssFile() {
	return cssFile;
    }

    public void setCssFile(String cssFile) {
	this.cssFile = cssFile;
    }

    public String getLogoAlign() {
	return logoAlign;
    }

    public void setLogoAlign(String logoAlign) {
	this.logoAlign = logoAlign;
    }

    public String getViewOutbox() {
	return this.viewOutbox;
    }

    public void setViewOutbox(String viewOutbox) {
	this.viewOutbox = viewOutbox;
    }

    public Long[] getOutboxItems() {
	return outboxItems;
    }

    public void setOutboxItems(Long[] outboxItems) {
	this.outboxItems = outboxItems;
    }

    public boolean isOutBoxEmpty() {
        return this.outBoxEmpty;
    }

    public void setOutBoxEmpty(boolean outBoxEmpty) {
        this.outBoxEmpty = outBoxEmpty;
    }

    public Boolean getShowOutbox() {
        return this.showOutbox;
    }

    public void setShowOutbox(Boolean showOutbox) {
        this.showOutbox = showOutbox;
    }

}