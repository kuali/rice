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
package edu.iu.uis.eden.actionlist.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts.action.ActionForm;

import edu.iu.uis.eden.actionlist.ActionToTake;

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

    public String getRouteLogPopup() {
        return routeLogPopup;
        //return Utilities.getApplicationConstant(EdenConstants.ACTION_LIST_ROUTE_LOG_POPUP_KEY).trim();
    }

    public void setRouteLogPopup(String routeLogPopup) {
        this.routeLogPopup = routeLogPopup;
    }

    public String getDocumentPopup() {
        return documentPopup;
        //return Utilities.getApplicationConstant(EdenConstants.ACTION_LIST_DOCUMENT_POPUP_KEY).trim();
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

//    public Long[] getFyiRequestSelect() {
//        return fyiRequestSelect;
//    }
//
//    public void setFyiRequestSelect(Long[] fyiRequestSelect) {
//        this.fyiRequestSelect = fyiRequestSelect;
//    }

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

}