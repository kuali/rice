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
package org.kuali.rice.kew.actionlist.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;
import org.displaytag.util.LookupUtil;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionitem.ActionItemActionListExtension;
import org.kuali.rice.kew.actionlist.ActionListFilter;
import org.kuali.rice.kew.actionlist.ActionToTake;
import org.kuali.rice.kew.actionlist.CustomActionListAttribute;
import org.kuali.rice.kew.actionlist.PaginatedActionList;
import org.kuali.rice.kew.actionlist.service.ActionListService;
import org.kuali.rice.kew.actions.ActionSet;
import org.kuali.rice.kew.actions.asyncservices.ActionInvocation;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.preferences.Preferences;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValueActionListExtension;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.PerformanceLogger;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.util.WebFriendlyRecipient;
import org.kuali.rice.kew.web.WorkflowAction;
import org.kuali.rice.kew.web.session.UserSession;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.util.KNSConstants;


/**
 * Action doing Action list stuff
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ActionListAction extends WorkflowAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ActionListAction.class);

    private static String ACTION_LIST_KEY = "actionList";
    private static String ACTION_LIST_PAGE_KEY = "actionListPage";
    private static String ACTION_LIST_USER_KEY = "actionList.user";
    private static String REQUERY_ACTION_LIST_KEY = "requeryActionList";

    public ActionForward start(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PerformanceLogger plog = new PerformanceLogger();
        plog.log("Starting ActionList fetch");
        ActionListForm form = (ActionListForm) actionForm;
        ActionErrors errors = new ActionErrors();
        ActionListService actionListSrv = KEWServiceLocator.getActionListService();


        // process display tag parameters
        Integer page = form.getPage();
        String sortCriterion = form.getSort();
        SortOrderEnum sortOrder = SortOrderEnum.ASCENDING;
        if (form.getDir() != null) {
        	sortOrder = parseSortOrder(form.getDir());
        }
        else if ( !StringUtils.isEmpty(getUserSession(request).getSortOrder()))     {
        	sortOrder = parseSortOrder(getUserSession(request).getSortOrder());
     //   	System.out.println("Session value for SortOrder "+sortOrder);
        }
        // if both the page and the sort criteria are null, that means its the first entry into the page, use defaults
        if (page == null && sortCriterion == null) {
        	page = new Integer(1);
        	sortCriterion = ActionItemComparator.DOCUMENT_ID;
        }
        else if ( !StringUtils.isEmpty(getUserSession(request).getSortCriteria()))     {
        	sortCriterion = getUserSession(request).getSortCriteria();
     //       System.out.println("Session sortCriterion variables used..."+getUserSession(request).getSortCriteria());
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
        List actionList = (List)request.getSession().getAttribute(ACTION_LIST_KEY);
        plog.log("Time to initialize");
        try {
            UserSession uSession = getUserSession(request);
            String principalId = null;
            if (uSession.getActionListFilter() == null) {
                ActionListFilter filter = new ActionListFilter();
                filter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
                filter.setExcludeDelegationType(true);
                uSession.setActionListFilter(filter);
            }

            /* 'forceListRefresh' variable used to signify that the action list filter has changed
             * any time the filter changes the action list must be refreshed or filter may not take effect on existing
             * list items... only exception is if action list has not loaded previous and fetching of the list has not
             * occurred yet
             */
            boolean forceListRefresh = request.getSession().getAttribute(REQUERY_ACTION_LIST_KEY) != null;
            if (uSession.getHelpDeskActionListUser() != null) {
            	principalId = uSession.getHelpDeskActionListUser().getPrincipalId();
            } else {
                if (!StringUtils.isEmpty(form.getDocType())) {
                    uSession.getActionListFilter().setDocumentType(form.getDocType());
                    uSession.getActionListFilter().setExcludeDocumentType(false);
                    forceListRefresh = true;
                }
                principalId = uSession.getPerson().getPrincipalId();
            }

            Preferences preferences = getUserSession(request).getPreferences();

            if (!StringUtils.isEmpty(form.getDelegationId())) {
                uSession.getActionListFilter().setDelegatorId(form.getDelegationId());
                uSession.getActionListFilter().setExcludeDelegatorId(false);
                actionList = null;
            }

            if (!StringUtils.isEmpty(form.getPrimaryDelegateId())) {
                uSession.getActionListFilter().setPrimaryDelegateId(form.getPrimaryDelegateId());
                actionList = null;
            }

            // if the user has changed, we need to refresh the action list
            if (!principalId.equals((String) request.getSession().getAttribute(ACTION_LIST_USER_KEY))) {
                actionList = null;
            }

            if (isOutboxMode(form, request, preferences)) {
        	actionList = new ArrayList(actionListSrv.getOutbox(principalId, uSession.getActionListFilter()));
        	form.setOutBoxEmpty(actionList.isEmpty());
            } else {
                if (actionList == null) {
                	// fetch the action list
                    actionList = new ArrayList(actionListSrv.getActionList(principalId, uSession.getActionListFilter()));
                    request.getSession().setAttribute(ACTION_LIST_USER_KEY, principalId);
            } else if (forceListRefresh) {
                // force a refresh... usually based on filter change or parameter specifying refresh needed
                    actionList = new ArrayList(actionListSrv.getActionList(principalId, uSession.getActionListFilter()));
                    request.getSession().setAttribute(ACTION_LIST_USER_KEY, principalId);
            } else if (actionListSrv.refreshActionList(getUserSession(request).getPerson().getPrincipalId())) {
                    actionList = new ArrayList(actionListSrv.getActionList(principalId, uSession.getActionListFilter()));
                    request.getSession().setAttribute(ACTION_LIST_USER_KEY, principalId);
                } else {
                	freshActionList = false;
                }
                request.getSession().setAttribute(ACTION_LIST_KEY, actionList);
            }
            // reset the requery action list key
            request.getSession().setAttribute(REQUERY_ACTION_LIST_KEY, null);

            // build the drop-down of delegators
            if (KEWConstants.DELEGATORS_ON_ACTION_LIST_PAGE.equalsIgnoreCase(preferences.getDelegatorFilter())) {
                Collection delegators = actionListSrv.findUserSecondaryDelegators(principalId);
                form.setDelegators(getWebFriendlyRecipients(delegators));
                form.setDelegationId(uSession.getActionListFilter().getDelegatorId());
                Collection delegates = actionListSrv.findUserPrimaryDelegations(principalId);
                form.setPrimaryDelegates(getWebFriendlyRecipients(delegates));
                form.setPrimaryDelegateId(uSession.getActionListFilter().getDelegatorId());
            }

            form.setFilterLegend(uSession.getActionListFilter().getFilterLegend());
            plog.log("Setting attributes");

            int pageSize = getPageSize(preferences);
            // initialize the action list if necessary
            if (freshActionList) {
            	initializeActionList(actionList, preferences, errors);
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
            PaginatedList currentPage = buildCurrentPage(actionList, form.getCurrentPage(), form.getCurrentSort(), form.getCurrentDir(), pageSize, preferences, errors, form);
            request.setAttribute(ACTION_LIST_PAGE_KEY, currentPage);
            uSession.setCurrentPage(form.getCurrentPage());
            uSession.setSortCriteria(form.getSort());
            uSession.setSortOrder(form.getCurrentDir());
            plog.log("finished setting attributes, finishing action list fetch");
        } catch (Exception e) {
            LOG.error("Error loading action list.", e);
        }

        saveErrors(request, errors);
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

	Person user = UserSession.getAuthenticatedUser().getPerson();

	if (! preferences.isUsingOutbox() || ! ConfigContext.getCurrentContextConfig().getOutBoxOn()) {
	    request.getSession().setAttribute(OUT_BOX_MODE, new Boolean(false));
	    alForm.setViewOutbox("false");
	    alForm.setShowOutbox(false);
	    return false;
	}

	alForm.setShowOutbox(true);
	if (StringUtils.isNotEmpty(alForm.getViewOutbox())) {
	    if (!new Boolean(alForm.getViewOutbox())) {
		request.getSession().setAttribute(OUT_BOX_MODE, new Boolean(false));
		outBoxView = false;
	    } else {
		request.getSession().setAttribute(OUT_BOX_MODE, new Boolean(true));
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

    private void sortActionList(List actionList, String sortName, SortOrderEnum sortOrder) {
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
    	for (Iterator iterator = actionList.iterator(); iterator.hasNext();) {
			ActionItemActionListExtension actionItem = (ActionItemActionListExtension) iterator.next();
			actionItem.setActionItemIndex(new Integer(index++));
		}
    }

    private void initializeActionList(List actionList, Preferences preferences, ActionErrors errors) throws WorkflowException {
    	List actionItemProblemIds = new ArrayList();
    	int index = 0;
    	for (Iterator iterator = actionList.iterator(); iterator.hasNext();) {
    		ActionItemActionListExtension actionItem = (ActionItemActionListExtension)iterator.next();
    		if (actionItem.getRouteHeaderId() == null) {
    			LOG.error("Somehow there exists an ActionItem with a null document id!  actionItemId=" + actionItem.getActionItemId());
    			iterator.remove();
    			continue;
    		}
    		try {
    			actionItem.initialize(preferences);
    			DocumentRouteHeaderValueActionListExtension routeHeaderExtension = (DocumentRouteHeaderValueActionListExtension)actionItem.getRouteHeader();
    			routeHeaderExtension.setActionListInitiatorPrincipal(routeHeaderExtension.getInitiatorPrincipal());
    			actionItem.setActionItemIndex(new Integer(index));
    			//set background colors for document statuses
    			if (KEWConstants.ROUTE_HEADER_APPROVED_CD.equalsIgnoreCase(actionItem.getRouteHeader().getDocRouteStatus())) {
    				actionItem.setRowStyleClass((String)KEWConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorApproved()));
    			} else if (KEWConstants.ROUTE_HEADER_CANCEL_CD.equalsIgnoreCase(actionItem.getRouteHeader().getDocRouteStatus())) {
    				actionItem.setRowStyleClass((String)KEWConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorCanceled()));
    			} else if (KEWConstants.ROUTE_HEADER_DISAPPROVED_CD.equalsIgnoreCase(actionItem.getRouteHeader().getDocRouteStatus())) {
    				actionItem.setRowStyleClass((String)KEWConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorDissaproved()));
    			} else if (KEWConstants.ROUTE_HEADER_ENROUTE_CD.equalsIgnoreCase(actionItem.getRouteHeader().getDocRouteStatus())) {
    				actionItem.setRowStyleClass((String)KEWConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorEnroute()));
    			} else if (KEWConstants.ROUTE_HEADER_EXCEPTION_CD.equalsIgnoreCase(actionItem.getRouteHeader().getDocRouteStatus())) {
    				actionItem.setRowStyleClass((String)KEWConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorException()));
    			} else if (KEWConstants.ROUTE_HEADER_FINAL_CD.equalsIgnoreCase(actionItem.getRouteHeader().getDocRouteStatus())) {
    				actionItem.setRowStyleClass((String)KEWConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorFinal()));
    			} else if (KEWConstants.ROUTE_HEADER_INITIATED_CD.equalsIgnoreCase(actionItem.getRouteHeader().getDocRouteStatus())) {
    				actionItem.setRowStyleClass((String)KEWConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorInitiated()));
    			} else if (KEWConstants.ROUTE_HEADER_PROCESSED_CD.equalsIgnoreCase(actionItem.getRouteHeader().getDocRouteStatus())) {
    				actionItem.setRowStyleClass((String)KEWConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorProccessed()));
    			} else if (KEWConstants.ROUTE_HEADER_SAVED_CD.equalsIgnoreCase(actionItem.getRouteHeader().getDocRouteStatus())) {
    				actionItem.setRowStyleClass((String)KEWConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorSaved()));
    			}
    			index++;
    		} catch (Exception e) {
    			// if there's a problem loading the action item, we don't want to blow out the whole screen but we will remove it from the list
    			// and display an approriate error message to the user
    			LOG.error("Error loading action list for action item " + actionItem.getActionItemId(), e);
    			iterator.remove();
    			actionItemProblemIds.add(actionItem.getRouteHeaderId());
    		}
    	}
    	generateActionItemErrors(errors, "actionlist.badActionItems", actionItemProblemIds);
    }

    /**
     * Gets the page size of the Action List.  Uses the user's preferences for page size unless the action list
     * has been throttled by an application constant, in which case it uses the smaller of the two values.
     */
    protected int getPageSize(Preferences preferences) {
    	int pageSize = Integer.parseInt(preferences.getPageSize());
    	String pageSizeThrottle = Utilities.getKNSParameterValue(KEWConstants.DEFAULT_KIM_NAMESPACE, KNSConstants.DetailTypes.ACTION_LIST_DETAIL_TYPE, KEWConstants.ACTION_LIST_PAGE_SIZE_THROTTLE);
    	if (!StringUtils.isEmpty(pageSizeThrottle)) {
    		try {
    			int throttle = Integer.parseInt(pageSizeThrottle);
    			if (throttle > 0 && throttle < pageSize) {
    				pageSize = throttle;
    			}
    		} catch (Exception e) {
    			LOG.warn("Encountered an error when parsing " + KEWConstants.ACTION_LIST_PAGE_SIZE_THROTTLE + ", " + e.getClass().getName() + ": " + e.getMessage());
    		}
    	}
    	return pageSize;
    }

    protected PaginatedList buildCurrentPage(List actionList, Integer page, String sortCriterion, String sortDirection, int pageSize, Preferences preferences, ActionErrors errors, ActionListForm form) throws WorkflowException {
    	List currentPage = new ArrayList(pageSize);
    	boolean haveFyis = false;
    	boolean haveApproves = false;
    	boolean haveAcknowledges = false;
    	boolean haveCancels = false;
    	boolean haveDisapproves = false;
    	boolean haveCustomActions = false;
    	List customActionListProblemIds = new ArrayList();
    	SortOrderEnum sortOrder = parseSortOrder(sortDirection);
    	int startIndex = (page.intValue() - 1) * pageSize;
    	int endIndex = startIndex + pageSize;
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
    			CustomActionListAttribute customActionListAttribute = actionItem.getRouteHeader().getCustomActionListAttribute();
    			if (customActionListAttribute != null) {
    				Map customActions = new LinkedHashMap();
    				customActions.put("NONE", "NONE");
    				ActionSet legalActions = customActionListAttribute.getLegalActions(UserSession.getAuthenticatedUser(), actionItem);
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
    				actionItem.setDisplayParameters(customActionListAttribute.getDocHandlerDisplayParameters(UserSession.getAuthenticatedUser(), actionItem));
    				haveApproves = haveApproves || itemHasApproves;
    				haveAcknowledges = haveAcknowledges || itemHasAcknowledges;
    				haveFyis = haveFyis || itemHasFyis;
    				haveDisapproves = haveDisapproves || itemHasDisapproves;
    				haveCancels = haveCancels || itemHasCancels;
    				haveCustomActions = haveCustomActions || itemHasCustomActions;
    			}
    		} catch (Exception e) {
    			// if there's a problem loading the custom action list attribute, let's go ahead and display the vanilla action item
    			LOG.error("Problem loading custom action list attribute", e);
    			customActionListProblemIds.add(actionItem.getRouteHeaderId());
    		}
    		currentPage.add(actionItem);
    	}

    	// configure custom actions on form
    	form.setHasCustomActions(new Boolean(haveCustomActions));
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

    	generateActionItemErrors(errors, "actionlist.badCustomActionListItems", customActionListProblemIds);
    	return new PaginatedActionList(currentPage, actionList.size(), page.intValue(), pageSize, "actionList", sortCriterion, sortOrder);
    }

    private void generateActionItemErrors(ActionErrors errors, String errorKey, List documentIds) {
    	if (!documentIds.isEmpty()) {
    		String documentIdsString = StringUtils.join(documentIds.iterator(), ", ");
    		errors.add(Globals.ERROR_KEY, new ActionMessage(errorKey, documentIdsString));
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
        for (Iterator iterator = actionListForm.getActionsToTake().iterator(); iterator.hasNext();) {
        	ActionToTake actionToTake = (ActionToTake) iterator.next();
        	if (actionToTake != null && actionToTake.getActionTakenCd() != null &&
        			!"".equals(actionToTake.getActionTakenCd()) &&
        			!"NONE".equalsIgnoreCase(actionToTake.getActionTakenCd()) &&
        			actionToTake.getActionItemId() != null) {
        		ActionItem actionItem = getActionItemFromActionList(actionList, actionToTake.getActionItemId());
        		if (actionItem == null) {
        			LOG.warn("Could not locate the ActionItem to take mass action against in the action list: " + actionToTake.getActionItemId());
        			continue;
        		}
            	invocations.add(new ActionInvocation(actionItem.getActionItemId(), actionToTake.getActionTakenCd()));
            }
        	index++;
		}
        KEWServiceLocator.getWorkflowDocumentService().takeMassActions(getUserSession(request).getPrincipalId(), invocations);
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("general.routing.processed"));
        saveMessages(request, messages);
        ActionListForm cleanForm = new ActionListForm();
        request.setAttribute(mapping.getName(), cleanForm);
        request.getSession().setAttribute(REQUERY_ACTION_LIST_KEY, "true");
        return start(mapping, cleanForm, request, response);
    }

    protected ActionItem getActionItemFromActionList(List actionList, Long actionItemId) {
    	for (Iterator iterator = actionList.iterator(); iterator.hasNext();) {
			ActionItem actionItem = (ActionItem) iterator.next();
			if (actionItem.getActionItemId().equals(actionItemId)) {
				return actionItem;
			}
		}
    	return null;
    }

    public ActionForward helpDeskActionListLogin(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionListForm actionListForm = (ActionListForm) form;
        Person helpDeskActionListUser = KIMServiceLocator.getPersonService().getPersonByPrincipalName(actionListForm.getHelpDeskActionListUserName());
        getUserSession(request).setHelpDeskActionListUser(helpDeskActionListUser);
        actionListForm.setDelegator(null);
        request.getSession().setAttribute(REQUERY_ACTION_LIST_KEY, "true");
        return start(mapping, form, request, response);
    }

    public ActionForward clearFilter(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("clearFilter ActionListAction");
        UserSession session = getUserSession(request);
        session.setActionListFilter(null);
        request.getSession().setAttribute(REQUERY_ACTION_LIST_KEY, "true");
        KEWServiceLocator.getActionListService().saveRefreshUserOption(getUserSession(request).getPerson().getPrincipalId());
        LOG.debug("end clearFilter ActionListAction");
        return start(mapping, form, request, response);
    }

    public ActionForward clearHelpDeskActionListUser(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("clearHelpDeskActionListUser ActionListAction");
        getUserSession(request).setHelpDeskActionListUser(null);
        LOG.debug("end clearHelpDeskActionListUser ActionListAction");
        return start(mapping, form, request, response);
    }

    /**
     * Generates an Action List count.
     */
    public ActionForward count(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ActionListForm alForm = (ActionListForm)form;
    	Person user = getUserSession(request).getPerson();
    	alForm.setCount(KEWServiceLocator.getActionListService().getCount(user.getPrincipalId()));
    	LOG.info("Fetched Action List count of " + alForm.getCount() + " for user " + user.getPrincipalName());
    	return mapping.findForward("count");
    }

    public ActionForward removeOutboxItems(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	ActionListForm alForm = (ActionListForm)form;
	if (alForm.getOutboxItems() != null) {
	    KEWServiceLocator.getActionListService().removeOutboxItems(getUserSession(request).getPrincipal().getPrincipalId(), Arrays.asList(alForm.getOutboxItems()));
	}

	alForm.setViewOutbox("true");
	return start(mapping, form, request, response);
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

    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        LOG.debug("establishRequiredState ActionListAction");
        ActionListForm actionListForm = (ActionListForm) form;

        // take the UserSession from the HttpSession and add it to the request
        request.setAttribute("UserSession", getUserSession(request));

        //refactor actionlist.jsp not to be dependent on this
        request.setAttribute("preferences", getUserSession(request).getPreferences());
        IdentityManagementService ims = (IdentityManagementService) KIMServiceLocator.getIdentityManagementService();
        String kewHelpDeskWgName = Utilities.getKNSParameterValue(KEWConstants.DEFAULT_KIM_NAMESPACE, KNSConstants.DetailTypes.ACTION_LIST_DETAIL_TYPE, KEWConstants.HELP_DESK_ACTION_LIST);
        if (kewHelpDeskWgName != null && KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(getUserSession(request).getPrincipalId(), KimConstants.TEMP_GROUP_NAMESPACE, kewHelpDeskWgName)) {
            request.setAttribute("helpDeskActionList", "true");
        }
        String routeLogPopup = "false";
        boolean routeLogPopupInd = Utilities.getKNSParameterBooleanValue(KEWConstants.DEFAULT_KIM_NAMESPACE, KNSConstants.DetailTypes.ACTION_LIST_DETAIL_TYPE, KEWConstants.ACTION_LIST_ROUTE_LOG_POPUP_IND);
        if (routeLogPopupInd) {
        	routeLogPopup = "true";
        }
        String documentPopup = "false";
        boolean documentPopupInd = Utilities.getKNSParameterBooleanValue(KEWConstants.DEFAULT_KIM_NAMESPACE, KNSConstants.DetailTypes.ACTION_LIST_DETAIL_TYPE, KEWConstants.ACTION_LIST_ROUTE_LOG_POPUP_IND);
        if (documentPopupInd) {
            documentPopup = "true";
        }
        actionListForm.setRouteLogPopup(routeLogPopup.trim());
        actionListForm.setDocumentPopup(documentPopup.trim());
        request.setAttribute("noRefresh", new Boolean(ConfigContext.getCurrentContextConfig().getProperty(KEWConstants.ACTION_LIST_NO_REFRESH)));
        LOG.debug("end establishRequiredState ActionListAction");
        return null;
    }

    public ActionMessages establishFinalState(HttpServletRequest request, ActionForm form) throws Exception {
        LOG.debug("establishFinalState ActionListAction");
        ActionListForm actionListForm = (ActionListForm) form;
        if (getUserSession(request).getHelpDeskActionListUser() != null) {
            actionListForm.setHelpDeskActionListUserName(getUserSession(request).getHelpDeskActionListUser().getPrincipalName());
        }
        LOG.debug("end establishFinalState ActionListAction");
        return null;
    }

    private List<WebFriendlyRecipient> getWebFriendlyRecipients(Collection recipients) {
        Collection newRecipients = new ArrayList(recipients.size());
        for (Iterator iterator = recipients.iterator(); iterator.hasNext();) {
            newRecipients.add(new WebFriendlyRecipient(iterator.next()));
        }
        List recipientList = new ArrayList(newRecipients);
        Collections.sort(recipientList, new Comparator() {
            Comparator comp = new ComparableComparator();

            public int compare(Object o1, Object o2) {
                return comp.compare(((WebFriendlyRecipient) o1).getDisplayName().trim().toLowerCase(), ((WebFriendlyRecipient) o2).getDisplayName().trim().toLowerCase());
            }
        });
        return recipientList;
    }


    private class ActionItemComparator implements Comparator {

    	private static final String DOCUMENT_ID = "routeHeaderId";

    	private final String sortName;

    	public ActionItemComparator(String sortName) {
    		if (StringUtils.isEmpty(sortName)) {
    			sortName = DOCUMENT_ID;
    		}
    		this.sortName = sortName;
    	}

		public int compare(Object object1, Object object2) {
			try {
				ActionItem actionItem1 = (ActionItem)object1;
				ActionItem actionItem2 = (ActionItem)object2;
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

}