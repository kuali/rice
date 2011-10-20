/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kew.actionlist.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;
import org.displaytag.util.LookupUtil;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionitem.ActionItemActionListExtension;
import org.kuali.rice.kew.actionlist.ActionListFilter;
import org.kuali.rice.kew.actionlist.ActionToTake;
import org.kuali.rice.kew.actionlist.CustomActionListAttribute;
import org.kuali.rice.kew.actionlist.PaginatedActionList;
import org.kuali.rice.kew.actionlist.service.ActionListService;
import org.kuali.rice.kew.actionrequest.Recipient;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.api.action.ActionInvocation;
import org.kuali.rice.kew.api.action.ActionItemCustomization;
import org.kuali.rice.kew.api.action.ActionSet;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.action.DelegationType;
import org.kuali.rice.kew.api.actionlist.DisplayParameters;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kew.doctype.DocumentTypeSecurity;
import org.kuali.rice.kew.doctype.SecuritySession;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.framework.KewFrameworkServiceLocator;
import org.kuali.rice.kew.framework.actionlist.ActionListCustomizationHandlerService;
import org.kuali.rice.kew.framework.document.security.DocumentSecurityDirective;
import org.kuali.rice.kew.framework.document.security.DocumentSecurityHandlerService;
import org.kuali.rice.kew.preferences.Preferences;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValueActionListExtension;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.PerformanceLogger;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.principal.PrincipalContract;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


/**
 * Action doing Action list stuff
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)a
 *
 */
public class ActionListAction extends KualiAction {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ActionListAction.class);

    private static final String ACTION_LIST_KEY = "actionList";
    private static final String ACTION_LIST_PAGE_KEY = "actionListPage";
    private static final String ACTION_LIST_USER_KEY = "actionList.user";
    private static final String REQUERY_ACTION_LIST_KEY = "requeryActionList";

    private static final String ACTIONREQUESTCD_PROP = "actionRequestCd";
    private static final String CUSTOMACTIONLIST_PROP = "customActionList";
    private static final String ACTIONITEM_PROP = "actionitem";
    private static final String HELPDESK_ACTIONLIST_USERNAME = "helpDeskActionListUserName";

    private static final String ACTIONITEM_ACTIONREQUESTCD_INVALID_ERRKEY = "actionitem.actionrequestcd.invalid";
    private static final String ACTIONLIST_BAD_CUSTOM_ACTION_LIST_ITEMS_ERRKEY = "actionlist.badCustomActionListItems";
	private static final String ACTIONLIST_BAD_ACTION_ITEMS_ERRKEY = "actionlist.badActionItems";
	private static final String HELPDESK_LOGIN_EMPTY_ERRKEY = "helpdesk.login.empty";
	private static final String HELPDESK_LOGIN_INVALID_ERRKEY = "helpdesk.login.invalid";


	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ActionListForm frm = (ActionListForm)actionForm;
    	request.setAttribute("Constants", getServlet().getServletContext().getAttribute("KEWConstants"));
    	request.setAttribute("preferences", getUserSession().retrieveObject(KEWConstants.PREFERENCES));
    	frm.setHeaderButtons(getHeaderButtons());
    	return super.execute(mapping, actionForm, request, response);
    }

    private List<ExtraButton> getHeaderButtons(){
    	List<ExtraButton> headerButtons = new ArrayList<ExtraButton>();
    	ExtraButton eb = new ExtraButton();
    	String krBaseUrl = ConfigContext.getCurrentContextConfig().getKRBaseURL();
    	eb.setExtraButtonSource( krBaseUrl + "/images/tinybutton-preferences.gif");
    	eb.setExtraButtonOnclick("Preferences.do?returnMapping=viewActionList");

    	headerButtons.add(eb);
    	eb = new ExtraButton();
    	eb.setExtraButtonSource(krBaseUrl + "/images/tinybutton-refresh.gif");
    	eb.setExtraButtonProperty("methodToCall.start");

    	headerButtons.add(eb);
    	eb = new ExtraButton();
    	eb.setExtraButtonSource(krBaseUrl + "/images/tinybutton-filter.gif");
    	eb.setExtraButtonOnclick("javascript: window.open('ActionListFilter.do?methodToCall=start');");
    	headerButtons.add(eb);


    	return headerButtons;
    }



	@Override
	protected ActionForward defaultDispatch(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return start(mapping, form, request, response);
	}

	public ActionForward start(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PerformanceLogger plog = new PerformanceLogger();
        plog.log("Starting ActionList fetch");
        ActionListForm form = (ActionListForm) actionForm;
        ActionListService actionListSrv = KEWServiceLocator.getActionListService();


        // process display tag parameters
        Integer page = form.getPage();
        String sortCriterion = form.getSort();
        SortOrderEnum sortOrder = SortOrderEnum.ASCENDING;
        final UserSession uSession = getUserSession();
        
        if (form.getDir() != null) {
        	sortOrder = parseSortOrder(form.getDir());
        }
        else if ( !StringUtils.isEmpty((String) uSession.retrieveObject(KEWConstants.SORT_ORDER_ATTR_NAME)))     {
        	sortOrder = parseSortOrder((String) uSession.retrieveObject(KEWConstants.SORT_ORDER_ATTR_NAME));
        }
        // if both the page and the sort criteria are null, that means its the first entry into the page, use defaults
        if (page == null && sortCriterion == null) {
        	page = Integer.valueOf(1);
        	sortCriterion = ActionItemComparator.DOCUMENT_ID;
        }
        else if ( !StringUtils.isEmpty((String) uSession.retrieveObject(KEWConstants.SORT_CRITERIA_ATTR_NAME)))     {
        	sortCriterion = (String) uSession.retrieveObject(KEWConstants.SORT_CRITERIA_ATTR_NAME);
        }
        // if the page is still null, that means the user just performed a sort action, pull the currentPage off of the form
        if (page == null) {
        	page = form.getCurrentPage();
        }

        // update the values of the "current" display tag parameters
        form.setCurrentPage(page);
        if (!StringUtils.isEmpty(sortCriterion)) {
        	form.setCurrentSort(sortCriterion);
        	form.setCurrentDir(getSortOrderValue(sortOrder));
        }

        // reset the default action on the form
        form.setDefaultActionToTake("NONE");

        boolean freshActionList = true;
        // retrieve cached action list
        List<ActionItem> actionList = (List<ActionItem>)request.getSession().getAttribute(ACTION_LIST_KEY);
        plog.log("Time to initialize");
        try {
            //UserSession uSession = getUserSession(request);
            String principalId = null;
            if (uSession.retrieveObject(KEWConstants.ACTION_LIST_FILTER_ATTR_NAME) == null) {
                ActionListFilter filter = new ActionListFilter();
                filter.setDelegationType(DelegationType.SECONDARY.getCode());
                filter.setExcludeDelegationType(true);
                uSession.addObject(KEWConstants.ACTION_LIST_FILTER_ATTR_NAME, filter);
            }

            final ActionListFilter filter = (ActionListFilter) uSession.retrieveObject(KEWConstants.ACTION_LIST_FILTER_ATTR_NAME);
            /* 'forceListRefresh' variable used to signify that the action list filter has changed
             * any time the filter changes the action list must be refreshed or filter may not take effect on existing
             * list items... only exception is if action list has not loaded previous and fetching of the list has not
             * occurred yet
             */
            boolean forceListRefresh = request.getSession().getAttribute(REQUERY_ACTION_LIST_KEY) != null;
            if (uSession.retrieveObject(KEWConstants.HELP_DESK_ACTION_LIST_PRINCIPAL_ATTR_NAME) != null) {
            	principalId = ((PrincipalContract) uSession.retrieveObject(KEWConstants.HELP_DESK_ACTION_LIST_PRINCIPAL_ATTR_NAME)).getPrincipalId();
            } else {
                if (!StringUtils.isEmpty(form.getDocType())) {
                	filter.setDocumentType(form.getDocType());
                	filter.setExcludeDocumentType(false);
                    forceListRefresh = true;
                }
                principalId = uSession.getPerson().getPrincipalId();
            }

            final Preferences preferences = (Preferences) getUserSession().retrieveObject(KEWConstants.PREFERENCES);

            if (!StringUtils.isEmpty(form.getDelegationId())) {
            	if (!KEWConstants.DELEGATION_DEFAULT.equals(form.getDelegationId())) {
            		// If the user can filter by both primary and secondary delegation, and both drop-downs have non-default values assigned,
            		// then reset the primary delegation drop-down's value when the primary delegation drop-down's value has remained unaltered
            		// but the secondary drop-down's value has been altered; but if one of these alteration situations does not apply, reset the
            		// secondary delegation drop-down.
            		if (StringUtils.isNotBlank(form.getPrimaryDelegateId()) && !KEWConstants.PRIMARY_DELEGATION_DEFAULT.equals(form.getPrimaryDelegateId())){
            			if (form.getPrimaryDelegateId().equals(request.getParameter("oldPrimaryDelegateId")) &&
            					!form.getDelegationId().equals(request.getParameter("oldDelegationId"))) {
            				form.setPrimaryDelegateId(KEWConstants.PRIMARY_DELEGATION_DEFAULT);
            			} else {
            				form.setDelegationId(KEWConstants.DELEGATION_DEFAULT);
            			}
            		} else if (StringUtils.isNotBlank(filter.getPrimaryDelegateId()) &&
            				!KEWConstants.PRIMARY_DELEGATION_DEFAULT.equals(filter.getPrimaryDelegateId())) {
            			// If the primary delegation drop-down is invisible but a primary delegation filter is in place, and if the secondary delegation
            			// drop-down has a non-default value selected, then reset the primary delegation filtering.
            			filter.setPrimaryDelegateId(KEWConstants.PRIMARY_DELEGATION_DEFAULT);
            		}
            	}
            	// Enable the secondary delegation filtering.
           		filter.setDelegatorId(form.getDelegationId());
           		filter.setExcludeDelegatorId(false);
           		actionList = null;
            }

            if (!StringUtils.isEmpty(form.getPrimaryDelegateId())) {
            	// If the secondary delegation drop-down is invisible but a secondary delegation filter is in place, and if the primary delegation
            	// drop-down has a non-default value selected, then reset the secondary delegation filtering.
            	if (StringUtils.isBlank(form.getDelegationId()) && !KEWConstants.PRIMARY_DELEGATION_DEFAULT.equals(form.getPrimaryDelegateId()) && 
            			StringUtils.isNotBlank(filter.getDelegatorId()) &&
            					!KEWConstants.DELEGATION_DEFAULT.equals(filter.getDelegatorId())) {
            		filter.setDelegatorId(KEWConstants.DELEGATION_DEFAULT);
            	}
            	// Enable the primary delegation filtering.
            	filter.setPrimaryDelegateId(form.getPrimaryDelegateId());
            	filter.setExcludeDelegatorId(false);
            	actionList = null;
            }
            
            // if the user has changed, we need to refresh the action list
            if (!principalId.equals(request.getSession().getAttribute(ACTION_LIST_USER_KEY))) {
                actionList = null;
            }

            if (isOutboxMode(form, request, preferences)) {
        	    actionList = new ArrayList<ActionItem>(actionListSrv.getOutbox(principalId, filter));
        	    form.setOutBoxEmpty(actionList.isEmpty());
            } else {
                if (actionList == null) {
                	//clear out old User Option records if they exist
                	actionListSrv.refreshActionList(getUserSession().getPerson().getPrincipalId());
                	// fetch the action list
                    actionList = new ArrayList<ActionItem>(actionListSrv.getActionList(principalId, filter));
                    request.getSession().setAttribute(ACTION_LIST_USER_KEY, principalId);
                } else if (forceListRefresh) {
                	// force a refresh... usually based on filter change or parameter specifying refresh needed
                    actionList = new ArrayList<ActionItem>(actionListSrv.getActionList(principalId, filter));
                    request.getSession().setAttribute(ACTION_LIST_USER_KEY, principalId);
                } else if (actionListSrv.refreshActionList(getUserSession().getPerson().getPrincipalId())) {
                    actionList = new ArrayList<ActionItem>(actionListSrv.getActionList(principalId, filter));
                    request.getSession().setAttribute(ACTION_LIST_USER_KEY, principalId);
                } else {
                	Boolean update = (Boolean) uSession.retrieveObject(KEWConstants.UPDATE_ACTION_LIST_ATTR_NAME);
                	if (update == null || !update) {
                		freshActionList = false;
                	}
                }
                request.getSession().setAttribute(ACTION_LIST_KEY, actionList);
            }
            // reset the requery action list key
            request.getSession().setAttribute(REQUERY_ACTION_LIST_KEY, null);

            // build the drop-down of delegators
            if (KEWConstants.DELEGATORS_ON_ACTION_LIST_PAGE.equalsIgnoreCase(preferences.getDelegatorFilter())) {
                Collection delegators = actionListSrv.findUserSecondaryDelegators(principalId);
                form.setDelegators(ActionListUtil.getWebFriendlyRecipients(delegators));
                form.setDelegationId(filter.getDelegatorId());
            }

            // Build the drop-down of primary delegates.
            if (KEWConstants.PRIMARY_DELEGATES_ON_ACTION_LIST_PAGE.equalsIgnoreCase(preferences.getPrimaryDelegateFilter())) {
            	Collection<Recipient> pDelegates = actionListSrv.findUserPrimaryDelegations(principalId);
            	form.setPrimaryDelegates(ActionListUtil.getWebFriendlyRecipients(pDelegates));
            	form.setPrimaryDelegateId(filter.getPrimaryDelegateId());
            }
            
            form.setFilterLegend(filter.getFilterLegend());
            plog.log("Setting attributes");

            int pageSize = getPageSize(preferences);
            // initialize the action list if necessary
            if (freshActionList) {
            	plog.log("calling initializeActionList");
            	initializeActionList(actionList, preferences);
            	plog.log("done w/ initializeActionList");
            	// put this in to resolve EN-112 (http://beatles.uits.indiana.edu:8081/jira/browse/EN-112)
                // if the action list gets "refreshed" in between page switches, we need to be sure and re-sort it, even though we don't have sort criteria on the request
            	if (sortCriterion == null) {
            		sortCriterion = form.getCurrentSort();
            		sortOrder = parseSortOrder(form.getCurrentDir());
            	}
            }
            // sort the action list if necessary
            if (sortCriterion != null) {
            	sortActionList(actionList, sortCriterion, sortOrder);
            }
            
            plog.log("calling buildCurrentPage");
            PaginatedList currentPage = buildCurrentPage(actionList, form.getCurrentPage(), form.getCurrentSort(), 
            			form.getCurrentDir(), pageSize, preferences, form);
            plog.log("done w/ buildCurrentPage");
            request.setAttribute(ACTION_LIST_PAGE_KEY, currentPage);
            uSession.addObject(KEWConstants.UPDATE_ACTION_LIST_ATTR_NAME, Boolean.FALSE);
            uSession.addObject(KEWConstants.CURRENT_PAGE_ATTR_NAME, form.getCurrentPage());
            uSession.addObject(KEWConstants.SORT_CRITERIA_ATTR_NAME, form.getSort());
            uSession.addObject(KEWConstants.SORT_ORDER_ATTR_NAME, form.getCurrentDir());
            plog.log("finished setting attributes, finishing action list fetch");
        } catch (Exception e) {
            LOG.error("Error loading action list.", e);
        }

        LOG.debug("end start ActionListAction");
        return mapping.findForward("viewActionList");
    }

    private SortOrderEnum parseSortOrder(String dir) throws WorkflowException {
    	if ("asc".equals(dir)) {
    		return SortOrderEnum.ASCENDING;
    	} else if ("desc".equals(dir)) {
    		return SortOrderEnum.DESCENDING;
    	}
    	throw new WorkflowException("Invalid sort direction: " + dir);
    }

    private String getSortOrderValue(SortOrderEnum sortOrder) {
    	if (SortOrderEnum.ASCENDING.equals(sortOrder)) {
    		return "asc";
    	} else if (SortOrderEnum.DESCENDING.equals(sortOrder)) {
    		return "desc";
    	}
    	return null;
    }

    private static final String OUT_BOX_MODE = "_OUT_BOX_MODE";

    /**
     * this method is setting 2 props on the {@link ActionListForm} that controls outbox behavior.
     *  alForm.setViewOutbox("false"); -> this is set by user preferences and the actionlist.outbox.off config prop
     *  alForm.setShowOutbox(false); -> this is set by user action clicking the ActionList vs. Outbox links.
     *
     * @param alForm
     * @param request
     * @return boolean indication whether the outbox should be fetched
     */
    private boolean isOutboxMode(ActionListForm alForm, HttpServletRequest request, Preferences preferences) {

	boolean outBoxView = false;

	if (! preferences.isUsingOutbox() || ! ConfigContext.getCurrentContextConfig().getOutBoxOn()) {
	    request.getSession().setAttribute(OUT_BOX_MODE, Boolean.valueOf(false));
	    alForm.setViewOutbox("false");
	    alForm.setShowOutbox(false);
	    return false;
	}

	alForm.setShowOutbox(true);
	if (StringUtils.isNotEmpty(alForm.getViewOutbox())) {
	    if (!Boolean.valueOf(alForm.getViewOutbox())) {
		request.getSession().setAttribute(OUT_BOX_MODE, Boolean.valueOf(false));
		outBoxView = false;
	    } else {
		request.getSession().setAttribute(OUT_BOX_MODE, Boolean.valueOf(true));
		outBoxView = true;
	    }
	} else {

	    if (request.getSession().getAttribute(OUT_BOX_MODE) == null) {
		outBoxView = false;
	    } else {
		outBoxView = (Boolean) request.getSession().getAttribute(OUT_BOX_MODE);
	    }
	}
	if (outBoxView) {
	    alForm.setViewOutbox("true");
	} else {
	    alForm.setViewOutbox("false");
	}
	return outBoxView;
    }

    private void sortActionList(List<ActionItem> actionList, String sortName, SortOrderEnum sortOrder) {
    	if (StringUtils.isEmpty(sortName)) {
    		return;
    	}
    	Comparator comparator = new ActionItemComparator(sortName);
    	if (SortOrderEnum.DESCENDING.equals(sortOrder)) {
    		comparator = ComparatorUtils.reversedComparator(comparator);
    	}
    	Collections.sort(actionList, comparator);
    	// re-index the action items
    	int index = 0;
    	for (ActionItem actionItem : actionList) {
			actionItem.setActionItemIndex(Integer.valueOf(index++));
		}
    }

    private void initializeActionList(List actionList, Preferences preferences) throws WorkflowException {
    	List actionItemProblemIds = new ArrayList();
    	Map<String,DocumentRouteHeaderValue> routeHeaders = KEWServiceLocator.getRouteHeaderService().getRouteHeadersForActionItems(org.kuali.rice.kew.actionitem.ActionItem.to(actionList));

    	int index = 0;
    	generateActionItemErrors(actionList);
    	//RouteHeaderService routeHeaderService = KEWServiceLocator.getRouteHeaderService();
    	for (Iterator iterator = actionList.iterator(); iterator.hasNext();) {
    		ActionItemActionListExtension actionItem = (ActionItemActionListExtension)iterator.next();
    		if (actionItem.getDocumentId() == null) {
    			LOG.error("Somehow there exists an ActionItem with a null document id!  actionItemId=" + actionItem.getId());
    			iterator.remove();
    			continue;
    		}
    		try {
    			actionItem.initialize(preferences);
    			DocumentRouteHeaderValue routeHeader = routeHeaders.get(actionItem.getDocumentId());
    			//DocumentRouteHeaderValue routeHeader = routeHeaderService.getRouteHeader(actionItem.getDocumentId());
    			DocumentRouteHeaderValueActionListExtension routeHeaderExtension = toDocumentRouteHeaderValueActionListExtension(routeHeader);
    			routeHeaderExtension.setActionListInitiatorPrincipal(routeHeaderExtension.getInitiatorPrincipal());
    			actionItem.setActionItemIndex(Integer.valueOf(index));
    			actionItem.setRouteHeader(routeHeaderExtension);
    			//set background colors for document statuses
    			if (KEWConstants.ROUTE_HEADER_CANCEL_CD.equalsIgnoreCase(routeHeader.getDocRouteStatus())) {
    				actionItem.setRowStyleClass(KEWConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorCanceled()));
    			} else if (KEWConstants.ROUTE_HEADER_DISAPPROVED_CD.equalsIgnoreCase(routeHeader.getDocRouteStatus())) {
    				actionItem.setRowStyleClass(KEWConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorDissaproved()));
    			} else if (KEWConstants.ROUTE_HEADER_ENROUTE_CD.equalsIgnoreCase(routeHeader.getDocRouteStatus())) {
    				actionItem.setRowStyleClass(KEWConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorEnroute()));
    			} else if (KEWConstants.ROUTE_HEADER_EXCEPTION_CD.equalsIgnoreCase(routeHeader.getDocRouteStatus())) {
    				actionItem.setRowStyleClass(KEWConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorException()));
    			} else if (KEWConstants.ROUTE_HEADER_FINAL_CD.equalsIgnoreCase(routeHeader.getDocRouteStatus())) {
    				actionItem.setRowStyleClass(KEWConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorFinal()));
    			} else if (KEWConstants.ROUTE_HEADER_INITIATED_CD.equalsIgnoreCase(routeHeader.getDocRouteStatus())) {
    				actionItem.setRowStyleClass(KEWConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorInitiated()));
    			} else if (KEWConstants.ROUTE_HEADER_PROCESSED_CD.equalsIgnoreCase(routeHeader.getDocRouteStatus())) {
    				actionItem.setRowStyleClass(KEWConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorProccessed()));
    			} else if (KEWConstants.ROUTE_HEADER_SAVED_CD.equalsIgnoreCase(routeHeader.getDocRouteStatus())) {
    				actionItem.setRowStyleClass(KEWConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorSaved()));
    			}
    			index++;
    		} catch (Exception e) {
    			// if there's a problem loading the action item, we don't want to blow out the whole screen but we will remove it from the list
    			// and display an approriate error message to the user
    			LOG.error("Error loading action list for action item " + actionItem.getId(), e);
    			iterator.remove();
    			actionItemProblemIds.add(actionItem.getDocumentId());
    		}
    	}
    	generateActionItemErrors(ACTIONITEM_PROP, ACTIONLIST_BAD_ACTION_ITEMS_ERRKEY, actionItemProblemIds);
    }

    private DocumentRouteHeaderValueActionListExtension toDocumentRouteHeaderValueActionListExtension(
			DocumentRouteHeaderValue routeHeader) {

		if(routeHeader==null){
			return null;
		}
		
		DocumentRouteHeaderValueActionListExtension extension = new DocumentRouteHeaderValueActionListExtension();
		
		extension.setDocumentId(routeHeader.getDocumentId());
		extension.setDocumentTypeId(routeHeader.getDocumentTypeId());
		extension.setDocRouteStatus(routeHeader.getDocRouteStatus());
		extension.setDocRouteLevel(routeHeader.getDocRouteLevel());
		extension.setStatusModDate(routeHeader.getStatusModDate());
		extension.setCreateDate(routeHeader.getCreateDate());
		extension.setApprovedDate(routeHeader.getApprovedDate());
		extension.setFinalizedDate(routeHeader.getFinalizedDate());
		extension.setRouteStatusDate(routeHeader.getRouteStatusDate());
		extension.setRouteLevelDate(routeHeader.getRouteLevelDate());
		extension.setDocTitle(routeHeader.getDocTitle());
		extension.setAppDocId(routeHeader.getAppDocId());
		extension.setDocVersion(routeHeader.getDocVersion());
		extension.setInitiatorWorkflowId(routeHeader.getInitiatorWorkflowId());
		extension.setVersionNumber(routeHeader.getVersionNumber());
		extension.setAppDocStatus(routeHeader.getAppDocStatus());
		extension.setAppDocStatusDate(routeHeader.getAppDocStatusDate());

		return extension;
	}
    
    /**
     * Gets the page size of the Action List.  Uses the user's preferences for page size unless the action list
     * has been throttled by an application constant, in which case it uses the smaller of the two values.
     */
    protected int getPageSize(Preferences preferences) {
    	return Integer.parseInt(preferences.getPageSize());
    }

    protected PaginatedList buildCurrentPage(List actionList, Integer page, String sortCriterion, String sortDirection, 
    		int pageSize, Preferences preferences, ActionListForm form) throws WorkflowException {
    	List currentPage = new ArrayList(pageSize);
    	boolean haveFyis = false;
    	boolean haveApproves = false;
    	boolean haveAcknowledges = false;
    	boolean haveCancels = false;
    	boolean haveDisapproves = false;
    	boolean haveCustomActions = false;
    	boolean haveDisplayParameters = false;
    	List customActionListProblemIds = new ArrayList();
    	SortOrderEnum sortOrder = parseSortOrder(sortDirection);
    	int startIndex = (page.intValue() - 1) * pageSize;
    	int endIndex = startIndex + pageSize;
    	generateActionItemErrors(actionList);
    	Map<String,DocumentRouteHeaderValue> routeHeaders = KEWServiceLocator.getRouteHeaderService().getRouteHeadersForActionItems(org.kuali.rice.kew.actionitem.ActionItem.to(actionList));
    	for (int index = startIndex; index < endIndex && index < actionList.size(); index++) {
    		ActionItemActionListExtension actionItem = (ActionItemActionListExtension)actionList.get(index);
    		// evaluate custom action list component for mass actions
    		try {
    			boolean itemHasApproves = false;
    			boolean itemHasDisapproves = false;
    			boolean itemHasCancels = false;
    			boolean itemHasAcknowledges = false;
    			boolean itemHasFyis = false;
    			boolean itemHasCustomActions = false;
    			// TODO see DocumentSecurityServiceImpl.checkAuthorizations to see how the Handler replaces the Attribute
    			CustomActionListAttribute customActionListAttribute = routeHeaders.get(actionItem.getDocumentId()).getCustomActionListAttribute();
    			if (customActionListAttribute != null) {
    				Map customActions = new LinkedHashMap();
    				customActions.put("NONE", "NONE");
    				ActionSet legalActions = customActionListAttribute.getLegalActions(getUserSession().getPrincipalId(), ActionItem.to(actionItem));
    				if (legalActions != null && legalActions.hasApprove() && isActionCompatibleRequest(actionItem, KEWConstants.ACTION_TAKEN_APPROVED_CD)) {
    					customActions.put(KEWConstants.ACTION_TAKEN_APPROVED_CD, KEWConstants.ACTION_REQUEST_APPROVE_REQ_LABEL);
    					itemHasApproves = true;
    				}
    				if (legalActions != null && legalActions.hasDisapprove() && isActionCompatibleRequest(actionItem, KEWConstants.ACTION_TAKEN_DENIED_CD)) {
    					customActions.put(KEWConstants.ACTION_TAKEN_DENIED_CD, KEWConstants.ACTION_REQUEST_DISAPPROVE_LABEL);
    					itemHasDisapproves = true;
    				}
    				if (legalActions != null && legalActions.hasCancel() && isActionCompatibleRequest(actionItem, KEWConstants.ACTION_TAKEN_CANCELED_CD)) {
    					customActions.put(KEWConstants.ACTION_TAKEN_CANCELED_CD, KEWConstants.ACTION_REQUEST_CANCEL_REQ_LABEL);
    					itemHasCancels = true;
    				}
    				if (legalActions != null && legalActions.hasAcknowledge() && isActionCompatibleRequest(actionItem, KEWConstants.ACTION_TAKEN_ACKNOWLEDGED_CD)) {
    					customActions.put(KEWConstants.ACTION_TAKEN_ACKNOWLEDGED_CD, KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
    					itemHasAcknowledges = true;
    				}
    				if (legalActions != null && legalActions.hasFyi() && isActionCompatibleRequest(actionItem, KEWConstants.ACTION_TAKEN_FYI_CD) && KEWConstants.PREFERENCES_YES_VAL.equalsIgnoreCase(preferences.getShowClearFyi())) {
    					customActions.put(KEWConstants.ACTION_TAKEN_FYI_CD, KEWConstants.ACTION_REQUEST_FYI_REQ_LABEL);
    					itemHasFyis = true;
    				}
    				if (customActions.size() > 1) {
    					actionItem.setCustomActions(customActions);
    					itemHasCustomActions = true;
    				}
    				actionItem.setDisplayParameters(customActionListAttribute.getDocHandlerDisplayParameters(getUserSession().getPrincipalId(), ActionItem.to(actionItem)));
    				haveApproves = haveApproves || itemHasApproves;
    				haveAcknowledges = haveAcknowledges || itemHasAcknowledges;
    				haveFyis = haveFyis || itemHasFyis;
    				haveDisapproves = haveDisapproves || itemHasDisapproves;
    				haveCancels = haveCancels || itemHasCancels;
    				haveCustomActions = haveCustomActions || itemHasCustomActions;
    				haveDisplayParameters = haveDisplayParameters || (actionItem.getDisplayParameters() != null);
    			}
    		} catch (Exception e) {
    			// if there's a problem loading the custom action list attribute, let's go ahead and display the vanilla action item
    			LOG.error("Problem loading custom action list attribute", e);
    			customActionListProblemIds.add(actionItem.getDocumentId());
    		}
    		currentPage.add(actionItem);
    	}

    	// configure custom actions on form
    	form.setHasCustomActions(Boolean.valueOf(haveCustomActions));
    	Map defaultActions = new LinkedHashMap();
    	defaultActions.put("NONE", "NONE");
    	if (haveApproves) {
    		defaultActions.put(KEWConstants.ACTION_TAKEN_APPROVED_CD, KEWConstants.ACTION_REQUEST_APPROVE_REQ_LABEL);
    		form.setCustomActionList(Boolean.TRUE);
    	}
    	if (haveDisapproves) {
    		defaultActions.put(KEWConstants.ACTION_TAKEN_DENIED_CD, KEWConstants.ACTION_REQUEST_DISAPPROVE_LABEL);
    		form.setCustomActionList(Boolean.TRUE);
    	}
    	if (haveCancels) {
    		defaultActions.put(KEWConstants.ACTION_TAKEN_CANCELED_CD, KEWConstants.ACTION_REQUEST_CANCEL_REQ_LABEL);
    		form.setCustomActionList(Boolean.TRUE);
    	}
    	if (haveAcknowledges) {
    		defaultActions.put(KEWConstants.ACTION_TAKEN_ACKNOWLEDGED_CD, KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
    		form.setCustomActionList(Boolean.TRUE);
    	}
    	//clearing FYI's can be done in any action list not just a customized one
    	if (haveFyis && KEWConstants.PREFERENCES_YES_VAL.equalsIgnoreCase(preferences.getShowClearFyi())) {
    		defaultActions.put(KEWConstants.ACTION_TAKEN_FYI_CD, KEWConstants.ACTION_REQUEST_FYI_REQ_LABEL);
    	}
    	if (defaultActions.size() > 1) {
    		form.setDefaultActions(defaultActions);
    	}

   		form.setHasDisplayParameters(haveDisplayParameters);
    	
    	generateActionItemErrors(CUSTOMACTIONLIST_PROP, ACTIONLIST_BAD_CUSTOM_ACTION_LIST_ITEMS_ERRKEY, customActionListProblemIds);
    	return new PaginatedActionList(currentPage, actionList.size(), page.intValue(), pageSize, "actionList", sortCriterion, sortOrder);
    }

    private void generateActionItemErrors(String propertyName, String errorKey, List documentIds) {
    	if (!documentIds.isEmpty()) {
    		String documentIdsString = StringUtils.join(documentIds.iterator(), ", ");
    		GlobalVariables.getMessageMap().putError(propertyName, errorKey, documentIdsString);
    	}
    }
    
    private void generateActionItemErrors(List actionList) {
    	for (Iterator iterator = actionList.iterator(); iterator.hasNext();) {
    		ActionItemActionListExtension actionItem = (ActionItemActionListExtension)iterator.next();
    		
    		// removing this check for the time being, it hinders action list performance. (KULRICE-2931)
//    		if (KEWServiceLocator.getRouteHeaderService().getRouteHeader(actionItem.getDocumentId()) == null) {
//    			GlobalVariables.getErrorMap().putError(ROUTEHEADERID_PROP, ACTIONITEM_ROUTEHEADERID_INVALID_ERRKEY,actionItem.getId()+"");
//    		}
    		
    		if(!KEWConstants.ACTION_REQUEST_CODES.containsKey(actionItem.getActionRequestCd())) {
    			GlobalVariables.getMessageMap().putError(ACTIONREQUESTCD_PROP,ACTIONITEM_ACTIONREQUESTCD_INVALID_ERRKEY,actionItem.getId()+"");
    		}
     	}
    }


    public ActionForward takeMassActions(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionListForm actionListForm = (ActionListForm) form;
        List actionList = (List) request.getSession().getAttribute(ACTION_LIST_KEY);
        if (actionList == null) {
            return start(mapping, new ActionListForm(), request, response);
        }
        ActionMessages messages = new ActionMessages();
        List invocations = new ArrayList();
        int index = 0;
        for (Object element : actionListForm.getActionsToTake()) {
        	ActionToTake actionToTake = (ActionToTake) element;
        	if (actionToTake != null && actionToTake.getActionTakenCd() != null &&
        			!"".equals(actionToTake.getActionTakenCd()) &&
        			!"NONE".equalsIgnoreCase(actionToTake.getActionTakenCd()) &&
        			actionToTake.getActionItemId() != null) {
        		ActionItem actionItem = getActionItemFromActionList(actionList, actionToTake.getActionItemId());
        		if (actionItem == null) {
        			LOG.warn("Could not locate the ActionItem to take mass action against in the action list: " + actionToTake.getActionItemId());
        			continue;
        		}
            	invocations.add(ActionInvocation.create(ActionType.fromCode(actionToTake.getActionTakenCd()), actionItem.getId()));
            }
        	index++;
		}
        KEWServiceLocator.getWorkflowDocumentService().takeMassActions(getUserSession().getPrincipalId(), invocations);
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("general.routing.processed"));
        saveMessages(request, messages);
        ActionListForm cleanForm = new ActionListForm();
        request.setAttribute(mapping.getName(), cleanForm);
        request.getSession().setAttribute(REQUERY_ACTION_LIST_KEY, "true");
        return start(mapping, cleanForm, request, response);
    }

    protected ActionItem getActionItemFromActionList(List<ActionItem> actionList, String actionItemId) {
    	for (ActionItem actionItem : actionList) {
			if (actionItem.getId().equals(actionItemId)) {
				return actionItem;
			}
		}
    	return null;
    }

    public ActionForward helpDeskActionListLogin(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionListForm actionListForm = (ActionListForm) form;
        String name = actionListForm.getHelpDeskActionListUserName();
        if (!"true".equals(request.getAttribute("helpDeskActionList"))) {
        	throw new AuthorizationException(getUserSession().getPrincipalId(), "helpDeskActionListLogin", getClass().getSimpleName());
        }
        try
        {
        	final Principal helpDeskActionListPrincipal = KEWServiceLocator.getIdentityHelperService().getPrincipalByPrincipalName(name);
        	final Person helpDeskActionListPerson = KEWServiceLocator.getIdentityHelperService().getPersonByPrincipalName(name);
        	
        	GlobalVariables.getUserSession().addObject(KEWConstants.HELP_DESK_ACTION_LIST_PRINCIPAL_ATTR_NAME, helpDeskActionListPrincipal);
        	GlobalVariables.getUserSession().addObject(KEWConstants.HELP_DESK_ACTION_LIST_PERSON_ATTR_NAME, helpDeskActionListPerson);
        }
        catch (RiceRuntimeException rre)
        {
        	GlobalVariables.getMessageMap().putError(HELPDESK_ACTIONLIST_USERNAME, HELPDESK_LOGIN_INVALID_ERRKEY, name);
        }
        catch (RiceIllegalArgumentException e) {
        	GlobalVariables.getMessageMap().putError(HELPDESK_ACTIONLIST_USERNAME, HELPDESK_LOGIN_INVALID_ERRKEY, name);
        }
        catch (NullPointerException npe)
        {
        	GlobalVariables.getMessageMap().putError("null", HELPDESK_LOGIN_EMPTY_ERRKEY, name);
        }
    	actionListForm.setDelegator(null);
        request.getSession().setAttribute(REQUERY_ACTION_LIST_KEY, "true");
        return start(mapping, form, request, response);
    }

    public ActionForward clearFilter(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("clearFilter ActionListAction");
        final org.kuali.rice.krad.UserSession commonUserSession = getUserSession();
        commonUserSession.removeObject(KEWConstants.ACTION_LIST_FILTER_ATTR_NAME);
        request.getSession().setAttribute(REQUERY_ACTION_LIST_KEY, "true");
        KEWServiceLocator.getActionListService().saveRefreshUserOption(commonUserSession.getPrincipalId());
        LOG.debug("end clearFilter ActionListAction");
        return start(mapping, form, request, response);
    }

    public ActionForward clearHelpDeskActionListUser(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("clearHelpDeskActionListUser ActionListAction");
    	GlobalVariables.getUserSession().removeObject(KEWConstants.HELP_DESK_ACTION_LIST_PRINCIPAL_ATTR_NAME);
    	GlobalVariables.getUserSession().removeObject(KEWConstants.HELP_DESK_ACTION_LIST_PERSON_ATTR_NAME);
        LOG.debug("end clearHelpDeskActionListUser ActionListAction");
        return start(mapping, form, request, response);
    }

    /**
     * Generates an Action List count.
     */
    public ActionForward count(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ActionListForm alForm = (ActionListForm)form;
    	Person user = getUserSession().getPerson();
    	alForm.setCount(KEWServiceLocator.getActionListService().getCount(user.getPrincipalId()));
    	LOG.info("Fetched Action List count of " + alForm.getCount() + " for user " + user.getPrincipalName());
    	return mapping.findForward("count");
    }

    public ActionForward removeOutboxItems(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	ActionListForm alForm = (ActionListForm)form;
	if (alForm.getOutboxItems() != null) {
	    KEWServiceLocator.getActionListService().removeOutboxItems(getUserSession().getPrincipalId(), Arrays.asList(alForm.getOutboxItems()));
	}

	alForm.setViewOutbox("true");
	return start(mapping, form, request, response);
    }

    /**
     * Navigate to the Action List Filter page, preserving any newly-modified primary/secondary delegation filters as necessary.
     */
    public ActionForward viewFilter(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	start(mapping, actionForm, request, response);
    	return mapping.findForward("viewFilter");
    }
    
    /**
     * Navigate to the user's Preferences page, preserving any newly-modified primary/secondary delegation filters as necessary.
     */
    public ActionForward viewPreferences(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	start(mapping, actionForm, request, response);
    	return mapping.findForward("viewPreferences");
    }
    
    private boolean isActionCompatibleRequest(ActionItemActionListExtension actionItem, String actionTakenCode) {
        boolean actionCompatible = false;
        String requestCd = actionItem.getActionRequestCd();

        //FYI request matches FYI
        if (KEWConstants.ACTION_REQUEST_FYI_REQ.equals(requestCd) && KEWConstants.ACTION_TAKEN_FYI_CD.equals(actionTakenCode)) {
            actionCompatible = true || actionCompatible;
        }

        // ACK request matches ACK
        if (KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ.equals(requestCd) && KEWConstants.ACTION_TAKEN_ACKNOWLEDGED_CD.equals(actionTakenCode)) {
            actionCompatible = true || actionCompatible;
        }

        // APPROVE request matches all but FYI and ACK
        if (KEWConstants.ACTION_REQUEST_APPROVE_REQ.equals(requestCd) && !(KEWConstants.ACTION_TAKEN_FYI_CD.equals(actionTakenCode) || KEWConstants.ACTION_TAKEN_ACKNOWLEDGED_CD.equals(actionTakenCode))) {
            actionCompatible = true || actionCompatible;
        }

        // COMPLETE request matches all but FYI and ACK
        if (KEWConstants.ACTION_REQUEST_COMPLETE_REQ.equals(requestCd) && !(KEWConstants.ACTION_TAKEN_FYI_CD.equals(actionTakenCode) || KEWConstants.ACTION_TAKEN_ACKNOWLEDGED_CD.equals(actionTakenCode))) {
            actionCompatible = true || actionCompatible;
        }

        return actionCompatible;
    }

	private UserSession getUserSession(){
		return GlobalVariables.getUserSession();
	}

    private static class ActionItemComparator implements Comparator<ActionItem> {

    	private static final String DOCUMENT_ID = "documentId";

    	private final String sortName;

    	public ActionItemComparator(String sortName) {
    		if (StringUtils.isEmpty(sortName)) {
    			sortName = DOCUMENT_ID;
    		}
    		this.sortName = sortName;
    	}

		@Override
		public int compare(ActionItem object1, ActionItem object2) {
			try {
				ActionItem actionItem1 = object1;
				ActionItem actionItem2 = object2;
				// invoke the power of the lookup functionality provided by the display tag library, this LookupUtil method allows for us
				// to evaulate nested bean properties (like workgroup.groupNameId.nameId) in a null-safe manner.  For example, in the
				// example if workgroup evaluated to NULL then LookupUtil.getProperty would return null rather than blowing an exception
				Object property1 = LookupUtil.getProperty(actionItem1, sortName);
				Object property2 = LookupUtil.getProperty(actionItem2, sortName);
				if (property1 == null && property2 == null) {
					return 0;
				} else if (property1 == null) {
					return -1;
				} else if (property2 == null) {
					return 1;
				}
				if (property1 instanceof Comparable) {
					return ((Comparable)property1).compareTo(property2);
				}
				return property1.toString().compareTo(property2.toString());
			} catch (Exception e) {
				if (e instanceof RuntimeException) {
					throw (RuntimeException) e;
				}
				throw new RuntimeException("Could not sort for the given sort name: " + sortName, e);
			}
		}
    }
    
    protected void processActionListCustomizations(String principalId, List<ActionItem> actionItems){
        if (CollectionUtils.isNotEmpty(actionItems)) {
            LOG.info("Beginning processing of Action List Customizations (total: "
                    + actionItems.size()
                    + " Action Items)");
            long start = System.currentTimeMillis();
            MultiValueMap<PartitionKey, ActionItem> partitions = partitionActionItems(actionItems);
            
            // TODO fill this out - DocumentSecurityServiceImpl.processDocumentRequiringExtensionProcessing
    
            long end = System.currentTimeMillis();
            LOG.info("Finished processing of Action List Customizations (total time: "
                    + (start - end)
                    + ")");
        }
    }
    
    protected MultiValueMap<PartitionKey,ActionItem> partitionActionItems(List<ActionItem> actionItems){
        // TODO fill this out - see DocumentSecurityServiceImpl.partitionDocumentsForSecurity
        MultiValueMap<PartitionKey, ActionItem> partitions = new LinkedMultiValueMap<PartitionKey, ActionItem>();

        return partitions;
    }
     
    protected ActionListCustomizationHandlerService loadActionListCustomizationHandler(String applicationId){
        ActionListCustomizationHandlerService service = KewFrameworkServiceLocator.getActionListCustomizationHandlerService(applicationId);
        if (service == null) {
            throw new WorkflowRuntimeException(
                    "Failed to locate ActionListCustomizationHandlerService for applicationId: " + applicationId);
        }
        return service;
    }
    
    /**
     * Simple class which defines the key of a partition of Action Items associated with an Application ID.
     *
     * <p>This class allows direct field access since it is intended for internal use only.</p>
     */
    private static final class PartitionKey {
        String applicationId;
        Set<String> customActionListAttributeNames;

        PartitionKey(String applicationId, Collection<ExtensionDefinition> extensionDefinitions) {
            this.applicationId = applicationId;
            this.customActionListAttributeNames = new HashSet<String>();
            for (ExtensionDefinition extensionDefinition : extensionDefinitions) {
                this.customActionListAttributeNames.add(extensionDefinition.getName());
            }
        }

        List<String> getCustomActionListAttributeNameList() {
            return new ArrayList<String>(customActionListAttributeNames);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof PartitionKey)) {
                return false;
            }
            PartitionKey key = (PartitionKey) o;
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(applicationId, key.applicationId);
            builder.append(customActionListAttributeNames, key.customActionListAttributeNames);
            return builder.isEquals();
        }

        @Override
        public int hashCode() {
            HashCodeBuilder builder = new HashCodeBuilder();
            builder.append(applicationId);
            builder.append(customActionListAttributeNames);
            return builder.hashCode();
        }
    }
}
