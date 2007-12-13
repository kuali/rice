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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionlist.ActionListFilter;
import edu.iu.uis.eden.actionlist.ActionListService;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.preferences.Preferences;
import edu.iu.uis.eden.preferences.PreferencesService;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.web.KeyValue;
import edu.iu.uis.eden.web.WorkflowAction;
import edu.iu.uis.eden.web.session.UserSession;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * Action for Action List Filter page.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionListFilterAction extends WorkflowAction {

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionListFilterForm filterForm = (ActionListFilterForm) form;
        if (getUserSession(request).getActionListFilter() != null) {
            ActionListFilter actionListFilter = getUserSession(request).getActionListFilter();
            if (filterForm.getDocTypeFullName() != null && ! "".equals(filterForm.getDocTypeFullName())) {
                actionListFilter.setDocumentType(filterForm.getDocTypeFullName());
                getUserSession(request).setActionListFilter(actionListFilter);
                filterForm.setFilter(actionListFilter);
            } else {
                filterForm.setFilter(actionListFilter);
                filterForm.setDocTypeFullName(actionListFilter.getDocumentType());
            }
        }
        return mapping.findForward("viewFilter");
    }

    public ActionForward filter(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionListFilterForm filterForm = (ActionListFilterForm) form;
        //validate the filter through the actionitem/actionlist service (I'm thinking actionlistservice)
        UserSession session = getUserSession(request);
        session.setActionListFilter(filterForm.getLoadedFilter());
        KEWServiceLocator.getActionListService().saveRefreshUserOption(session.getWorkflowUser());
        return mapping.findForward("viewActionList");
    }

    public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionListFilterForm filterForm = (ActionListFilterForm) form;
        filterForm.setFilter(new ActionListFilter());
        filterForm.setCreateDateFrom("");
        filterForm.setCreateDateTo("");
        filterForm.setLastAssignedDateFrom("");
        filterForm.setLastAssignedDateTo("");
        filterForm.setDocTypeFullName("");
        UserSession session = getUserSession(request);
        session.setActionListFilter(null);
        KEWServiceLocator.getActionListService().saveRefreshUserOption(session.getWorkflowUser());
        return mapping.findForward("viewFilter");
    }

    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        ActionListFilterForm filterForm = (ActionListFilterForm) form;
        filterForm.setUserWorkgroups(getUserWorkgroupsDropDownList(getUserSession(request).getWorkflowUser()));
        PreferencesService prefSrv = (PreferencesService) KEWServiceLocator.getPreferencesService();
        Preferences preferences = prefSrv.getPreferences(getUserSession(request).getWorkflowUser());
        request.setAttribute("preferences", preferences);
        ActionListService actionListSrv = (ActionListService) KEWServiceLocator.getActionListService();
        Collection delegators = actionListSrv.findUserDelegators(getUserSession(request).getWorkflowUser(), EdenConstants.DELEGATION_SECONDARY);
        request.setAttribute("delegators", getWebFriendlyRecipients(delegators));
        if (! filterForm.getMethodToCall().equalsIgnoreCase("clear")) {
            filterForm.validateDates();
        }
        return null;
    }

    private List getUserWorkgroupsDropDownList(WorkflowUser user) throws EdenUserNotFoundException {
    	List userWorkgroups = KEWServiceLocator.getWorkgroupService().getUsersGroups(user);
    	List sortedUserWorkgroups = new ArrayList();
    	KeyValue keyValue = null;
    	keyValue = new KeyValue(EdenConstants.NO_FILTERING, EdenConstants.NO_FILTERING);
    	sortedUserWorkgroups.add(keyValue);
    	if (userWorkgroups != null && userWorkgroups.size() > 0) {
    		Collections.sort(userWorkgroups, new Comparator() {
    			Comparator comp = new ComparableComparator();
    			public int compare(Object o1, Object o2) {
    				return comp.compare(((Workgroup) o1).getGroupNameId().getNameId().trim().toLowerCase(), ((Workgroup) o2).getGroupNameId().getNameId().trim().toLowerCase());
    			}
    		});

    		for (Iterator iter = userWorkgroups.iterator(); iter.hasNext();) {
    			Workgroup workgroup = (Workgroup) iter.next();
    			keyValue = new KeyValue(workgroup.getWorkflowGroupId().getGroupId().toString(), workgroup.getGroupNameId().getNameId());
    			sortedUserWorkgroups.add(keyValue);
    		}
    	}
    	return sortedUserWorkgroups;
    }

    private List getWebFriendlyRecipients(Collection recipients) {
        Collection newRecipients = new ArrayList(recipients.size());
        for (Iterator iterator = recipients.iterator(); iterator.hasNext();) {
            newRecipients.add(new WebFriendlyRecipient((Recipient) iterator.next()));
        }
        List recipientList = new ArrayList(newRecipients);
        Collections.sort(recipientList, new Comparator() {
            Comparator comp = new ComparableComparator();
            public int compare(Object o1, Object o2) {
                return comp.compare(((WebFriendlyRecipient)o1).getDisplayName().trim().toLowerCase(), ((WebFriendlyRecipient)o2).getDisplayName().trim().toLowerCase());
            }
        });
        return recipientList;
    }

    public class WebFriendlyRecipient {
        private String displayName;
        private String recipientId;
        public WebFriendlyRecipient(Recipient recipient) {
            if (recipient instanceof Workgroup) {
                recipientId = ((Workgroup)recipient).getWorkflowGroupId().getGroupId().toString();
                displayName = recipient.getDisplayName();
            } else if (recipient instanceof WorkflowUser) {
                recipientId = ((WorkflowUser)recipient).getWorkflowUserId().getWorkflowId();
                displayName = ((WorkflowUser)recipient).getTransposedName();
            }
        }
        public String getRecipientId() {
            return recipientId;
        }
        public String getDisplayName() {
            return displayName;
        }
    }

}
